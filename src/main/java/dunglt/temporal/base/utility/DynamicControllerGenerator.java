package dunglt.temporal.base.utility;

import dunglt.temporal.api.controller.DynamicControllerTemplate;
import dunglt.temporal.api.dto.DataDTO;
import dunglt.temporal.base.model.MActivity;
import dunglt.temporal.base.model.MRestConfig;
import dunglt.temporal.base.service.ConnectionService;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;

@Component
public class DynamicControllerGenerator {

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    public Class<?> generateController(MActivity activity) {
        MRestConfig config = connectionService.getRestConfigByActivityIdAndType(
                activity.getActivityId(), TemporalConstant.REST_CONFIG_TYPE_SEND);

        if (config == null){
            return null;
        }

        String url = config.getUrl();
        String httpMethod = config.getHttpMethod();
        String className = activity.getActivityType()
                .replaceAll("[^a-zA-Z0-9_]", "_")
                .replaceAll("_+", "_");

        try {
            return new ByteBuddy()
                    .subclass(DynamicControllerTemplate.class)
                    .name("dunglt.temporal.api.controller.Generated_" +
                            className  + "_Controller")
                    .annotateType(AnnotationDescription.Builder
                            .ofType(RequestMapping.class)
                            .defineArray("value", "/api/" + url)
                            .build())
                    .method(net.bytebuddy.matcher.ElementMatchers.named("executeWorkflow"))
                    .intercept(MethodCall.invoke(
                                    DynamicControllerTemplate.class
                                            .getDeclaredMethod("executeWorkflow", DataDTO.class, String.class))
                            .onSuper()
                            .withAllArguments())
                    .annotateMethod(createMethodAnnotation(httpMethod))
                    .make()
                    .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();
        }catch (Exception e){
            throw new RuntimeException("Failed to generate controller for activity: " + activity.getActivityType(), e);
        }
    }

    private AnnotationDescription createMethodAnnotation(String httpMethod) {
        Class<? extends Annotation> annotationClass = switch (httpMethod.toUpperCase()) {
            case "POST" -> PostMapping.class;
            case "GET" -> GetMapping.class;
            case "PUT" -> PutMapping.class;
            case "DELETE" -> DeleteMapping.class;
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + httpMethod);
        };

        return AnnotationDescription.Builder.ofType(annotationClass).build();
    }

    private void registerControllerInSpring(Class<?> controllerClass) throws Exception {
        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();

        Object controllerInstance = beanFactory.createBean(controllerClass);

        String beanName = controllerClass.getSimpleName();
        ((org.springframework.beans.factory.support.DefaultListableBeanFactory) beanFactory)
                .registerSingleton(beanName, controllerInstance);

        Method detectHandlerMethods = null;
        Class<?> clazz = requestMappingHandlerMapping.getClass();
        while (clazz != null) {
            try {
                detectHandlerMethods = clazz.getDeclaredMethod("detectHandlerMethods", Object.class);
                break;
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
            }
        }

        if (detectHandlerMethods == null) {
            throw new NoSuchMethodException("detectHandlerMethods not found in hierarchy");
        }

        detectHandlerMethods.setAccessible(true);
        detectHandlerMethods.invoke(requestMappingHandlerMapping, controllerInstance);
    }


    public void generateAndRegister(MActivity activity)  {
        Class<?> controllerClass = generateController(activity);
        if (controllerClass != null) {
            try {
                registerControllerInSpring(controllerClass);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void unregisterAllGeneratedControllers() {
        org.springframework.beans.factory.support.DefaultListableBeanFactory beanFactory =
                (org.springframework.beans.factory.support.DefaultListableBeanFactory)
                        applicationContext.getAutowireCapableBeanFactory();

        // Tìm tất cả bean có tên bắt đầu bằng "Generated_" và xóa khỏi Spring
        java.util.List<String> toRemove = new ArrayList<>();
        for (String beanName : beanFactory.getSingletonNames()) {
            if (beanName.startsWith("Generated_")) {
                toRemove.add(beanName);
            }
        }

        for (String beanName : toRemove) {
            Object instance = beanFactory.getSingleton(beanName);

            // Lấy tất cả RequestMappingInfo của bean này rồi unregister từng cái
            requestMappingHandlerMapping.getHandlerMethods().forEach((mappingInfo, handlerMethod) -> {
                if (handlerMethod.getBean().equals(instance)) {
                    requestMappingHandlerMapping.unregisterMapping(mappingInfo);
                }
            });

            beanFactory.destroySingleton(beanName);
            System.out.println("Unregistered controller: " + beanName);
        }
    }

}
