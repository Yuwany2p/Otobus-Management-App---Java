package Master.User;

public class User {
    String NIK, Nama, Username, Password, Email, Jabatan;
    int ID_Setting, No;

    public User(int No, String NIK, String nama, int ID_Setting,String Jabatan, String username, String password, String email ) {
        this.No = No;
        this.NIK = NIK;
        Nama = nama;
        Username = username;
        Password = password;
        Email = email;
        this.ID_Setting = ID_Setting;
        this.Jabatan = Jabatan;
    }

    public User() {
    }

    public String getNIK() {
        return NIK;
    }

    public void setNIK(String NIK) {
        this.NIK = NIK;
    }

    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        Nama = nama;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public int getID_Setting() {
        return ID_Setting;
    }

    public void setID_Setting(int ID_Setting) {
        this.ID_Setting = ID_Setting;
    }

    public String getJabatan() {
        return Jabatan;
    }

    public void setJabatan(String jabatan) {
        Jabatan = jabatan;
    }

    public int getNo() {
        return No;
    }

    public void setNo(int no) {
        No = no;
    }
}
