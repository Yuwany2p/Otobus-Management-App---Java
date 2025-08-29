package Transaksi.PembelianTiket;

import DBConnect.DBConnect;
import Master.Tiket.Tiket;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class tiketController implements Initializable {
    @FXML
    private Label lblEstimasi;

    @FXML
    private Label lblHarga;

    @FXML
    private Label lblPergi;

    @FXML
    private Label lblRuteAsal;

    @FXML
    private Label lblRuteTujuan;

    @FXML
    private Label lblSisaKursi;

    @FXML
    private Label lblTiba;

    @FXML
    private Label lblTipeBus;

    @FXML
    private Button btnTambah;


    private Tiket tiket;
    private AnchorPane rootPane;
    private PembelianTiketController parentController;

    public void setParent(PembelianTiketController parentController) {
        this.parentController = parentController;
    }

    public void setRootPane(AnchorPane rootPane) {
        this.rootPane = rootPane;
    }

    public void setData(Tiket tiket) {
        this.tiket = tiket;

        lblPergi.setText(formatTanggal2Baris(tiket.getKeberangkatan()));
        lblTiba.setText(formatTanggal2Baris(tiket.getKedatangan()));
        lblHarga.setText(String.format("%,.0f", tiket.getHarga()));
        lblRuteAsal.setText(tiket.getRute_Asal());
        lblRuteTujuan.setText(tiket.getRute_Tujuan());
        lblSisaKursi.setText(String.valueOf(tiket.getJumlah_Tiket()));
        lblTipeBus.setText(tiket.getNomor_Polisi());
        lblEstimasi.setText(tiket.getJarak() + "KM");
    }

    private String formatTanggal2Baris(String datetime) {
        if (datetime == null || datetime.length() <= 10) {
            return datetime != null ? datetime : "";
        }
        return datetime.substring(0, 10) + "\n" + datetime.substring(10).trim();
    }

    public Button getBtnTambah() {
        return btnTambah;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnTambah.setOnAction(event -> handleTambah());
    }

    @FXML
    private void handleTambah() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Transaksi/layoutKursi.fxml"));
            AnchorPane popupContent = loader.load();

            layoutKursiController controller = loader.getController();
            controller.setPembelianTiketController(parentController);
            controller.setTiket(tiket);
            parentController.setTiket(tiket);


            // kirim tiket yang aktif
            controller.setRootPane(rootPane);

            // tampilkan popup seperti sebelumnya
            AnchorPane overlay = new AnchorPane();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
            overlay.prefWidthProperty().bind(rootPane.widthProperty());
            overlay.prefHeightProperty().bind(rootPane.heightProperty());

            popupContent.setLayoutX((rootPane.getWidth() - popupContent.getPrefWidth()) / 2);
            popupContent.setLayoutY((rootPane.getHeight() - popupContent.getPrefHeight()) / 2);

            overlay.getChildren().add(popupContent);
            rootPane.getChildren().add(overlay);

            controller.setOverlay(overlay);
            controller.setVbSelectedTiket(parentController.getVbSelectedTiket());

            overlay.setOnMouseClicked(event -> rootPane.getChildren().remove(overlay));
            popupContent.setOnMouseClicked(event -> event.consume());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
