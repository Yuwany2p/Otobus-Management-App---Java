package Master.Bus;

public class Bus {
    private String ID_Bus;
    private int ID_Setting;
    private String NoPolisi;
    private int Tersedia;
    private int Kapasitas;
    private String TipeBus;
    private int No;

    public int getNo() {
        return No;
    }

    public void setNo(int no) {
        No = no;
    }

    public Bus(int No, String ID_Bus , int Id_Setting, String NoPolisi, int Tersedia, int Kapasitas, String TipeBus) {
        this.No=No;
        this.ID_Bus = ID_Bus;
        this.ID_Setting = Id_Setting;
        this.NoPolisi = NoPolisi;
        this.Tersedia = Tersedia;
        this.Kapasitas = Kapasitas;
        this.TipeBus = TipeBus;
    }

    public Bus(String ID_Bus ,String NoPolisi) {
        this.No=0;
        this.ID_Bus = ID_Bus;
        this.ID_Setting = 0;
        this.NoPolisi = NoPolisi;
        this.Tersedia = 0;
        this.Kapasitas = 0;
        this.TipeBus = "";
    }

    public String getID_Bus() {
        return ID_Bus;
    }

    public void setID_Bus(String ID_Bus) {
        this.ID_Bus = ID_Bus;
    }

    public int getID_Setting() {
        return ID_Setting;
    }

    public void setID_Setting(int ID_Setting) {
        this.ID_Setting = ID_Setting;
    }

    public String getNoPolisi() {
        return NoPolisi;
    }

    public void setNoPolisi(String noPolisi) {
        NoPolisi = noPolisi;
    }

    public int getTersedia() {
        return Tersedia;
    }

    public void setTersedia(int tersedia) {
        Tersedia = tersedia;
    }

    public int getKapasitas() {
        return Kapasitas;
    }

    public void setKapasitas(int kapasitas) {
        Kapasitas = kapasitas;
    }

    public String getTipeBus() {
        return TipeBus;
    }

    public void setTipeBus(String tipeBus) {
        TipeBus = tipeBus;
    }

    @Override
    public String toString() {
        return getNoPolisi();
    }
}
