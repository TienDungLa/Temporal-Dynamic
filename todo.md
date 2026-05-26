MAIN GOAL:
- Tạo một hệ thống dynamic để quản lý các workflow và activity, có 
UI để trực quan sử dụng quản lý các worker và thêm mới các config
trong quá trính runtime.


LIST CÁC VIỆC CẦN LÀM (KHÔNG THEO THỨ TỰ ƯU TIÊN)

- Tạo UI bằng ZK để quản lý workflow + activity
- Dùng Byte Buddy để tạo controller động trong runtime
- Làm luồng thêm mới worker cho facory trong runtime - DONE
- Nghiên cứu cách validate dto động - tạm thời bỏ vì dùng JNODE
- Sử dụng notifiActivity để áp dụng saga pattern cho workflow


ZZZ

long running task = phải có response method để xử lý async

- A(0) :
- A(1) : gửi request từ Ochres đến service 1 (s1)
- A(2) : gửi request từ Ochres đến service 2 (s2)

Happy path rest api:
- A(0) -> O (requst gọi controller)
- 0 -> A(0) (status 200, đã nhận request, chuẩn bị process workflow)
- A(1): send request đến s1
- A(1): nhận response từ s1, nếu thành công (status 200), mở websocket để nhận respone, responseData của A(1) = sendData của A(2)
- A(2): send request đến s2
- A(2): nhận response từ s2, nếu thành công (status 200), done wf
- O: notify cho cả 2 service