package Jadwal;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class cardJadwalBusController {

    @FXML
    private Label lblAsal;

    @FXML
    private Label lblNo;

    @FXML
    private Label lblNomorPolisi;

    @FXML
    private Label lblTanggal;

    @FXML
    private Label lblTujuan;

    public void setData(JadwalBus jadwalBus) {
        lblNo.setText(String.valueOf(jadwalBus.getNo()));
        lblNomorPolisi.setText(jadwalBus.getNomorPolisi());
        lblTanggal.setText(jadwalBus.getKeberangkatan());
        lblAsal.setText(jadwalBus.getRuteAsal());
        lblTujuan.setText(jadwalBus.getRuteTujuan());
    }
}
