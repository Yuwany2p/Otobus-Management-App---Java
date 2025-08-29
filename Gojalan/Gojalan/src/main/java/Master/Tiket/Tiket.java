package Master.Tiket;

public class Tiket {
    private String ID_Tiket;
    private String NIK_Supir;
    private String Nama;
    private String ID_Bus;
    private String ID_Rute;
    private String Rute_Tujuan;
    private String Rute_Asal;
    private int Jarak;
    private int ID_Setting;
    private String keberangkatan;
    private String Kedatangan;
    private double Harga;
    private int Jumlah_Tiket;
    private String Nomor_Polisi;
    private int No;
    private int Kapasitas;

    public Tiket(String ID_Bus, String Nomor_Polisi, int a) {
        this.ID_Bus = ID_Bus;
        this.Nomor_Polisi = Nomor_Polisi;
    }

    public Tiket(String NIK_Supir, String nama) {
        this.NIK_Supir = NIK_Supir;
        Nama = nama;
    }

    private Tiket(String ID, int b, String tujuan){
        ID_Rute = ID;
        Rute_Tujuan = tujuan;
    }

    public int getKapasitas() {
        return Kapasitas;
    }

    public void setKapasitas(int kapasitas) {
        Kapasitas = kapasitas;
    }

    public Tiket() {}

    public Tiket(int No, String ID_Tiket, String NIK_Supir, String NamaSupir, String ID_Bus, String Nomor_Polisi, int kapasitas, String ID_Rute, String rute_Asal, String rute_Tujuan, int Jarak,  int ID_Setting, String kedatangan, String keberangkatan, double harga, int jumlah_Tiket) {
        this.ID_Tiket = ID_Tiket;
        this.NIK_Supir = NIK_Supir;
        this.ID_Bus = ID_Bus;
        this.ID_Rute = ID_Rute;
        this.ID_Setting = ID_Setting;
        this.keberangkatan = keberangkatan;
        this.Kedatangan = kedatangan;
        Harga = harga;
        Jumlah_Tiket = jumlah_Tiket;
        this.No = No;
        Nama = NamaSupir;
        this.Rute_Tujuan = rute_Tujuan;
        this.Rute_Asal = rute_Asal;
        this.Jarak = Jarak;
        this.Nomor_Polisi = Nomor_Polisi;
        this.Kapasitas = kapasitas;
    }

    public Tiket(String Nama_Supir, String kedatangan, String keberangkatan, Double harga, int Jumlah_Tiket) {
        this.Nama = Nama_Supir;
        this.Kedatangan = kedatangan;
        this.keberangkatan = keberangkatan;
        this.Harga = harga;
        this.Jumlah_Tiket = Jumlah_Tiket;
    }

    public String getRute_Tujuan() {
        return Rute_Tujuan;
    }

    public void setRute_Tujuan(String rute_Tujuan) {
        Rute_Tujuan = rute_Tujuan;
    }

    public String getRute_Asal() {
        return Rute_Asal;
    }

    public void setRute_Asal(String rute_Asal) {
        Rute_Asal = rute_Asal;
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

    public String getID_Tiket() {
        return ID_Tiket;
    }

    public void setID_Tiket(String ID_Tiket) {
        this.ID_Tiket = ID_Tiket;
    }

    public String getNIK_Supir() {
        return NIK_Supir;
    }

    public void setNIK_Supir(String NIK_Supir) {
        this.NIK_Supir = NIK_Supir;
    }

    public String getID_Bus() {
        return ID_Bus;
    }

    public void setID_Bus(String ID_Bus) {
        this.ID_Bus = ID_Bus;
    }

    public String getID_Rute() {
        return ID_Rute;
    }

    public void setID_Rute(String ID_Rute) {
        this.ID_Rute = ID_Rute;
    }

    public int getID_Setting() {
        return ID_Setting;
    }



    public void setkedatangan(String kedatangan) {
        kedatangan = kedatangan;
    }

    public void setID_Setting(int ID_Setting) {
        this.ID_Setting = ID_Setting;
    }

    public String getKeberangkatan() {
        return keberangkatan;
    }

    public void setKeberangkatan(String keberangkatan) {
        this.keberangkatan = keberangkatan;
    }

    public String getKedatangan() {
        return Kedatangan;
    }

    public void setKedatangan(String kedatangan) {
        Kedatangan = kedatangan;
    }

    public String getNomor_Polisi() {
        return Nomor_Polisi;
    }

    public void setNomor_Polisi(String nomor_Polisi) {
        Nomor_Polisi = nomor_Polisi;
    }

    public double getHarga() {
        return Harga;
    }

    public void setHarga(double harga) {
        Harga = harga;
    }

    public int getJumlah_Tiket() {
        return Jumlah_Tiket;
    }

    public void setJumlah_Tiket(int jumlah_Tiket) {
        Jumlah_Tiket = jumlah_Tiket;
    }
    public String getNama() {
        return Nama;
    }

    public void setNama(String nama_Supir) {
        Nama = nama_Supir;
    }

    @Override
    public String toString(){
        return Nomor_Polisi;
    }
}
