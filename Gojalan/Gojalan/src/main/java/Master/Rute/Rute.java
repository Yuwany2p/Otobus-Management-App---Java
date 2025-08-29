package Master.Rute;

public class Rute {
    private String ID_Rute;
    private String RuteAsal;
    private String RuteTujuan;
    private int Jarak;
    private int No;

    public Rute(int No,String ID_Rute, String RuteAsal, String RuteTujuan, int Jarak) {
        this.No = No;
        this.ID_Rute = ID_Rute;
        this.RuteAsal = RuteAsal;
        this.RuteTujuan = RuteTujuan;
        this.Jarak = Jarak;
    }

    public Rute(String ID_Rute, String RuteTujuan) {
        this.ID_Rute = ID_Rute;
        this.RuteTujuan = RuteTujuan;
        this.RuteAsal = "";
        this.Jarak = 0;
    }

    public Rute(String ruteAsal) {
        this.RuteAsal = ruteAsal;
    }


    public String getID_Rute() {
        return ID_Rute;
    }

    public void setID_Rute(String ID_Rute) {
        this.ID_Rute = ID_Rute;
    }

    public String getRuteAsal() {
        return RuteAsal;
    }

    public void setRuteAsal(String ruteAsal) {
        RuteAsal = ruteAsal;
    }

    public String getRuteTujuan() {
        return RuteTujuan;
    }

    public void setRuteTujuan(String ruteTujuan) {
        RuteTujuan = ruteTujuan;
    }

    public int getJarak() {
        return Jarak;
    }

    public void setJarak(int jarak) {
        Jarak = jarak;
    }

    public int getNo() {
        return No;
    }

    public void setNo(int no) {
        No = no;
    }

    public String toString() {
        return getRuteTujuan(); // Ini yang akan ditampilkan di ComboBox
    }
}
