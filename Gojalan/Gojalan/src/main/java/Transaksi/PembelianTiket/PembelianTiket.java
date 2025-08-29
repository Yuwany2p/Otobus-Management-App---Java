package Transaksi.PembelianTiket;

import java.util.Date;

public class PembelianTiket {
    private String ID_PembelianTiket;
    private String NIK_User;
    private String ID_Tiket;
    private String Keberangkatan;
    private String Kepulangan;
    private String Rute_Asal;
    private String Rute_Tujuan;
    private String Tgl_Pembelian;
    private String No_Telepon;
    private String Alamat;
    private String Nama;
    private double Harga;
    private int banyak_kursi;
    private int NO;


    public PembelianTiket(int NO,String ID_PembelianTiket, String NIK_User, String ID_Tiket, String Keberangkatan, String Kepulangan, String Rute_Asal, String Rute_Tujuan, String tgl_Pembelian, String no_Telepon, String alamat, String nama, double harga, int banyak_kursi) {
        this.ID_PembelianTiket = ID_PembelianTiket;
        this.NIK_User = NIK_User;
        this.ID_Tiket = ID_Tiket;
        this.Keberangkatan = Keberangkatan;
        this.Kepulangan = Kepulangan;
        this.Rute_Asal = Rute_Asal;
        this.Rute_Tujuan = Rute_Tujuan;
        Tgl_Pembelian = tgl_Pembelian;
        No_Telepon = no_Telepon;
        Alamat = alamat;
        Nama = nama;
        Harga = harga;
        this.banyak_kursi = banyak_kursi;
        this.NO = NO;
    }

    public int getBanyak_kursi() {
        return banyak_kursi;
    }

    public int getNO() {
        return NO;
    }

    public void setNO(int NO) {
        this.NO = NO;
    }

    public void setBanyak_kursi(int banyak_kursi) {
        this.banyak_kursi = banyak_kursi;
    }

    public String getKeberangkatan() {
        return Keberangkatan;
    }

    public void setKeberangkatan(String keberangkatan) {
        Keberangkatan = keberangkatan;
    }

    public String getKepulangan() {
        return Kepulangan;
    }

    public void setKepulangan(String kepulangan) {
        Kepulangan = kepulangan;
    }

    public String getRute_Asal() {
        return Rute_Asal;
    }

    public void setRute_Asal(String rute_Asal) {
        Rute_Asal = rute_Asal;
    }

    public String getRute_Tujuan() {
        return Rute_Tujuan;
    }

    public void setRute_Tujuan(String rute_Tujuan) {
        Rute_Tujuan = rute_Tujuan;
    }

    public String getID_PembelianTiket() {
        return ID_PembelianTiket;
    }

    public void setID_PembelianTiket(String ID_PembelianTiket) {
        this.ID_PembelianTiket = ID_PembelianTiket;
    }

    public String getNIK_User() {
        return NIK_User;
    }

    public void setNIK_User(String NIK_User) {
        this.NIK_User = NIK_User;
    }

    public String getID_Tiket() {
        return ID_Tiket;
    }

    public void setID_Tiket(String ID_Tiket) {
        this.ID_Tiket = ID_Tiket;
    }

    public String getTgl_Pembelian() {
        return Tgl_Pembelian;
    }

    public void setTgl_Pembelian(String tgl_Pembelian) {
        Tgl_Pembelian = tgl_Pembelian;
    }

    public String getNo_Telepon() {
        return No_Telepon;
    }

    public void setNo_Telepon(String no_Telepon) {
        No_Telepon = no_Telepon;
    }

    public String getAlamat() {
        return Alamat;
    }

    public void setAlamat(String alamat) {
        Alamat = alamat;
    }

    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        Nama = nama;
    }

    public double getHarga() {
        return Harga;
    }

    public void setHarga(double harga) {
        Harga = harga;
    }
}
