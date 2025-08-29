package Master.Supir;

public class Supir {
    private String NIK;
    private String Nama;
    private String Email;
    private String Alamat;
    private String NoSIM;
    private String NoTelepon;
    private int No;

    public Supir(int No,String NIK, String Nama, String Email, String alamat, String noSIM,String NoTelepon) {
        this.No = No;
        this.NIK = NIK;
        this.Nama = Nama;
        this.Email = Email;
        this.Alamat = alamat;
        this.NoTelepon = NoTelepon;
        this.NoSIM = noSIM;
    }

    public Supir(String NIk, String nama) {
        NIK = NIk;
        Nama = nama;
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

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAlamat() {
        return Alamat;
    }

    public void setAlamat(String alamat) {
        Alamat = alamat;
    }

    public String getNoSIM() {
        return NoSIM;
    }

    public void setNoSIM(String noSIM) {
        NoSIM = noSIM;
    }

    public String getNoTelepon() {
        return NoTelepon;
    }

    public void setNoTelepon(String noTelepon) {
        NoTelepon = noTelepon;
    }

    public int getNo() {
        return No;
    }

    public void setNo(int no) {
        No = no;
    }

    public String toString() {
        return getNama(); // Ini yang akan ditampilkan di ComboBox
    }

}
