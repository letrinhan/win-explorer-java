# Tên sinh viên: Lê Trí Nhân
# Lớp: 17IT2
# MSSV: 17IT081
# Câu 1: Thiết kế giao diện phần mềm file Explore.
# Câu 2: Thực hiện chức năng dán(paste) 1 tập tin.
# Chức năng thêm vào: open file, copy và dán 1 thư mục bất kì
# Cách chạy code
               1.Tải project và giải nén project từ github
	       
               2.Mở cmd
		
               3.Dùng lệnh cd chuyển đến thư mục chứa source code
                ví dụ: C:\Users\PC\Desktop\thigiuaky\src
               4.Biên dịch bằng lệnh javac: javac\explorer.java
                5.Chạy bằng lệnh java java.explorer
# Giới thiệu FileExplorer
	1.1. Chạy FileExplorer.java.
	1.2. Cửa sổ của File Explorer
   	 - Cửa sổ bên trái: là cây thư mục liệt kê tất cả các tài nguyên của máy: các ổ đĩa, thư mục, tập tin,…
    	    + Click trái lên đối tượng (ổ đĩa, thư mục,…) thì nội dung bên trong của đối tượng sẽ hiện lên ở khung bên phải.
     	    + Click trái vào dấu (+) đứng trước một đối tượng thì trong cây thư mục sẽ hiện tiếp các thư mục con của đối tượng này.
     	    + Click trái vào dấu (-) đứng trước một đối tượng thì trong cây thư mục sẽ được ẩn.
    	 - Cửa sổ bên phải: liệt kê chi tiết (gồm các thư mục con và tập tin) của đối tượng được chọn trong cửa sổ bên trái.
    	 - Thanh trên cùng có: nút "Back", nút "Next", một khung chỉ đường dẫn File, khung tìm kiếm và nút "Search".
    	 - Thanh cuối cùng có: nút "Open", nút "Copy" và nút "Paste".
2. Các chức năng
	2.1. Mở thư mục: Khi click lên đối tượng (thư mục hoặc tệp tin) bên cửa sổ bên phải và nhấn vào nút "Open" thì đối tượng đó sẽ được mở lên và hiển thị những file có trong đối tượng ở cửa sổ bên phải.
	2.2. Di chuyển thư mục, tệp tin
  	  - Click trái vào thư mục hay tệp tin cần sao chép ở cửa sổ bên phải hay trái đều được rồi nhấn nút "Copy".
   	  - Click trái vào nơi muốn chuyển thư mục hay tệp tin đến và nhấn nút "Paste".
link youtube:https://www.youtube.com/watch?v=BYKUi3utbW0
