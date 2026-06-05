MAIN GOAL:
- Tạo một hệ thống dynamic để quản lý các workflow và activity, có 
UI để trực quan sử dụng quản lý các worker và thêm mới các config
trong quá trính runtime.


LIST CÁC VIỆC CẦN LÀM (KHÔNG THEO THỨ TỰ ƯU TIÊN)

- Tạo UI bằng ZK để quản lý workflow + activity
- Dùng Byte Buddy để tạo controller động trong runtime DONE
- Làm luồng thêm mới worker cho facory trong runtime - DONE
- Nghiên cứu cách validate dto động - tạm thời bỏ vì dùng DODE
- Sử dụng notifiActivity để áp dụng saga pattern cho workflow  DONE

LUỒNG API CƠ BẢN
- Controller -> WorkflowTriggerService -> WorkflowClientService
-> WorkflowClient -> WorkflowExecution -> ActivityExecution -> Activity