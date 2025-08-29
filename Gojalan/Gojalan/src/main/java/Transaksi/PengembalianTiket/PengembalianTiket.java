package Transaksi.PengembalianTiket;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class PengembalianTiket {
    private String ID_Pembelian;
    private List<String> ID_Kursi= new ArrayList<>();
    private int Status;
    private double persenPengembalian;
    private double totalPengembalian;
    private Date TanggalPembatalan;

    public PengembalianTiket(String ID_Pembelian, List<String> ID_Kursi, int status, double persenPengembalian, double totalPengembalian, Date tanggalPembatalan) {
        this.ID_Pembelian = ID_Pembelian;
        this.ID_Kursi = ID_Kursi;
        Status = status;
        this.persenPengembalian = persenPengembalian;
        this.totalPengembalian = totalPengembalian;
        TanggalPembatalan = tanggalPembatalan;
    }

    public String getID_Pembelian() {
        return ID_Pembelian;
    }

    public void setID_Pembelian(String ID_Pembelian) {
        this.ID_Pembelian = ID_Pembelian;
    }

    public List<String> getID_Kursi() {
        return ID_Kursi;
    }

    public void setID_Kursi(List<String> ID_Kursi) {
        this.ID_Kursi = ID_Kursi;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
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

    public Date getTanggalPembatalan() {
        return TanggalPembatalan;
    }

    public void setTanggalPembatalan(Date tanggalPembatalan) {
        TanggalPembatalan = tanggalPembatalan;
    }
}
