package Master.Kursi;

public class Kursi {
    private String ID_Kursi;
    private String ID_Bus;
    private String NoPolisi;
    private String NoKursi;
    private int Tersedia;
    private int No;

    public int getNo() {
        return No;
    }

    public void setNo(int no) {
        No = no;
    }

    public Kursi(int No ,String ID_Kursi, String NoPolisi, String noKursi, int tersedia, String Id_Bus) {
        this.No = No;
        this.ID_Kursi = ID_Kursi;
        this.ID_Bus = Id_Bus;
        this.NoPolisi = NoPolisi;
        NoKursi = noKursi;
        Tersedia = tersedia;
    }

    public Kursi(String ID_Bus, String NoPolisi) {
        this.NoPolisi = NoPolisi;
        this.ID_Bus = ID_Bus;
    }

    public Kursi(String ID_Kursi, String No_Kursi, String ID_Bus) {
        this.ID_Kursi = ID_Kursi;
        this.NoKursi = No_Kursi;
        this.ID_Bus = ID_Bus;
    }


    public String getNoPolisi() {
        return NoPolisi;
    }

    public void setNoPolisi(String noPolisi) {
        NoPolisi = noPolisi;
    }

    public String getID_Kursi() {
        return ID_Kursi;
    }

    public void setID_Kursi(String ID_Kursi) {
        this.ID_Kursi = ID_Kursi;
    }

    public String getID_Bus() {
        return ID_Bus;
    }

    public void setID_Bus(String ID_Bus) {
        this.ID_Bus = ID_Bus;
    }

    public String getNamaBus() {
        return NoPolisi;
    }

    public void setNamaBus(String NoPolisi) {
        this.NoPolisi = NoPolisi;
    }

    public String getNoKursi() {
        return NoKursi;
    }

    public void setNoKursi(String noKursi) {
        NoKursi = noKursi;
    }

    public int getTersedia() {
        return Tersedia;
    }

    public void setTersedia(int tersedia) {
        Tersedia = tersedia;
    }

    @Override
    public String toString() {
        return NoKursi; // Ini yang akan ditampilkan di ComboBox
    }
}
