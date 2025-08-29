package Master.Setting;

public class Setting {
    int id_setting, No;
    String nama, value, kategori1, kategori2;

    public Setting(int No, int id_setting, String nama, String value, String kategori1, String kategori2) {
        this.No = No;
        this.id_setting = id_setting;
        this.nama = nama;
        this.value = value;
        this.kategori1 = kategori1;
        this.kategori2 = kategori2;

    }

    public Setting(int id_setting, String value) {
        this.id_setting = id_setting;
        this.nama = null;
        this.value = value;
        this.kategori1 = null;
        this.kategori2 = null;

    }

    public int getId_setting() {
        return id_setting;
    }

    public void setId_setting(int id_setting) {
        this.id_setting = id_setting;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKategori1() {
        return kategori1;
    }

    public void setKategori1(String kategori1) {
        this.kategori1 = kategori1;
    }

    public String getKategori2() {
        return kategori2;
    }

    public void setKategori2(String kategori2) {
        this.kategori2 = kategori2;
    }

    public int getNo() {
        return No;
    }

    public void setNo(int no) {
        No = no;
    }

    @Override
    public String toString() {
        return value; // Ini yang akan ditampilkan di ComboBox
    }


}
