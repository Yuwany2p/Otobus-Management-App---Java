package Transaksi.PembelianTiket;

public class DetailPembelianTiket {
    private int No;
    private String idPembelianTiket;
    private String idKursi;
    private int status;
    private double persenPengembalian;
    private double totalPengembalian;
    private String tanggalPembatalan;
    private String NoKursi;
    private String Id_Tiket;
    private double Harga;

    public DetailPembelianTiket(int No,String idPembelianTiket, String idKursi, int status, String NoKursi, double persenPengembalian, double totalPengembalian, String tanggalPembatalan,String Id_Tiket, double harga) {
        this.No = No;
        this.idPembelianTiket = idPembelianTiket;
        this.idKursi = idKursi;
        this.status = status;
        this.persenPengembalian = persenPengembalian;
        this.totalPengembalian = totalPengembalian;
        this.tanggalPembatalan = tanggalPembatalan;
        this.NoKursi = NoKursi;
        this.Id_Tiket = Id_Tiket;
        this.Harga = harga;
    }

    public int getNo() {
        return No;
    }

    public void setNo(int no) {
        No = no;
    }

    public String getIdPembelianTiket() {
        return idPembelianTiket;
    }

    public void setIdPembelianTiket(String idPembelianTiket) {
        this.idPembelianTiket = idPembelianTiket;
    }

    public String getIdKursi() {
        return idKursi;
    }

    public void setIdKursi(String idKursi) {
        this.idKursi = idKursi;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getPersenPengembalian() {
        return persenPengembalian;
    }

    public void setPersenPengembalian(double persenPengembalian) {
        this.persenPengembalian = persenPengembalian;
    }

    public double getTotalPengembalian() {
        return totalPengembalian;
    }

    public void setTotalPengembalian(double totalPengembalian) {
        this.totalPengembalian = totalPengembalian;
    }

    public String getTanggalPembatalan() {
        return tanggalPembatalan;
    }

    public void setTanggalPembatalan(String tanggalPembatalan) {
        this.tanggalPembatalan = tanggalPembatalan;
    }

    public String getNoKursi() {
        return NoKursi;
    }

    public void setNoKursi(String noKursi) {
        NoKursi = noKursi;
    }

    public String getId_Tiket() {
        return Id_Tiket;
    }

    public void setId_Tiket(String id_Tiket) {
        Id_Tiket = id_Tiket;
    }

    public double getHarga() {
        return Harga;
    }

    public void setHarga(double harga) {
        Harga = harga;
    }
}
