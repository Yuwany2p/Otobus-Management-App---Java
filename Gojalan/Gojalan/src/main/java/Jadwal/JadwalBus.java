package Jadwal;

public class JadwalBus {
    private int No;
    private String idBus;
    private String nomorPolisi;
    private String idRute;
    private String ruteAsal;
    private String ruteTujuan;
    private String keberangkatan;


    // Constructor
    public JadwalBus(int No, String idBus, String nomorPolisi, String idRute, String ruteAsal, String ruteTujuan, String keberangkatan) {
        this.No = No;
        this.idBus = idBus;
        this.nomorPolisi = nomorPolisi;
        this.idRute = idRute;
        this.ruteAsal = ruteAsal;
        this.ruteTujuan = ruteTujuan;
        this.keberangkatan = keberangkatan;
    }

    public int getNo() {
        return No;
    }

    public void setNo(int no) {
        No = no;
    }

    public String getIdBus() {
        return idBus;
    }

    public void setIdBus(String idBus) {
        this.idBus = idBus;
    }

    public String getNomorPolisi() {
        return nomorPolisi;
    }

    public void setNomorPolisi(String nomorPolisi) {
        this.nomorPolisi = nomorPolisi;
    }

    public String getIdRute() {
        return idRute;
    }

    public void setIdRute(String idRute) {
        this.idRute = idRute;
    }

    public String getRuteAsal() {
        return ruteAsal;
    }

    public void setRuteAsal(String ruteAsal) {
        this.ruteAsal = ruteAsal;
    }

    public String getRuteTujuan() {
        return ruteTujuan;
    }

    public void setRuteTujuan(String ruteTujuan) {
        this.ruteTujuan = ruteTujuan;
    }

    public String getKeberangkatan() {
        return keberangkatan;
    }

    public void setKeberangkatan(String keberangkatan) {
        this.keberangkatan = keberangkatan;
    }
}
