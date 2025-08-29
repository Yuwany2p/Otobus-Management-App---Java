package com.example.gojalan;

import Master.User.userCRUDController;
import Transaksi.PembelianTiket.PembelianTiketController;
import Transaksi.PengembalianTiket.PembatalanTiketController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class DashboardAgenController  {

    @FXML
    private Label LbName;

    @FXML
    private Label LbTab;

    @FXML
    private Pane panelPenanda;

    @FXML
    private AnchorPane pnlContent;

    @FXML
    private Pane pnlKeluar;

    @FXML
    private Pane panelHalamanAwal;

    @FXML
    private Pane panelPembelianTiket;

    @FXML
    private Pane panelPengembalianTiket;

    @FXML
    private Pane panelJadwalBus;

    @FXML
    private AnchorPane popupLayer;

    @FXML
    private Pane darkOverlay;

    @FXML
    private Label lblEdit;

    @FXML
    private ImageView imgKeluar;

    @FXML
    private Label lblKeluar;


    String username = "";
    public String getUsername() {
        return username;
    }


    public void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            AnchorPane pane = loader.load();
            Object controller = loader.getController();

            if (controller instanceof PembelianTiketController) {
                PembelianTiketController usr = (PembelianTiketController) controller;
                usr.setUsername(username);
            }

            if (controller instanceof PembatalanTiketController) {
                PembatalanTiketController usr = (PembatalanTiketController) controller;
                usr.setUsername(username);
            }

            pnlContent.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHalamanAwal() {
        LbTab.setText("Halaman Awal");
        loadPage("/com/example/gojalan/Agen/HalamanAwal.fxml");
        panelPenanda.setLayoutY(panelHalamanAwal.getLayoutY());
    }

    @FXML
    private void handlePembelian() {
        panelPenanda.setLayoutY(panelPembelianTiket.getLayoutY());
        LbTab.setText("Pembelian Tiket");
        loadPage("/com/example/gojalan/Agen/PembelianTiket.fxml");
    }

    @FXML
    private void handlePengembalian() {
        panelPenanda.setLayoutY(panelPengembalianTiket.getLayoutY());
        LbTab.setText("Pengembalian Tiket");
        loadPage("/com/example/gojalan/Agen/Pengembalian.fxml");
    }


    @FXML
    private void handleJadwalBus() {
        panelPenanda.setLayoutY(panelJadwalBus.getLayoutY());
        LbTab.setText("Jadwal Bus");
        loadPage("/com/example/gojalan/Agen/JadwalBus.fxml");
    }


    public DashboardAgenController() {
    }

    public void setData(String nama, String usr) {
        username=usr;
        LbName.setText(nama);
    }

    @FXML
    private void entered(){
        lblEdit.setStyle("-fx-text-fill: gray");
    }

    @FXML
    private void exited(){
        lblEdit.setStyle("");
    }

    @FXML
    private void keluarEntered() {
        lblKeluar.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");
        imgKeluar.setFitHeight(80);
        imgKeluar.setFitWidth(80);
    }

    @FXML
    private void keluarExited(){
        lblKeluar.setStyle("");
        imgKeluar.setFitHeight(70);
        imgKeluar.setFitWidth(70);
    }


    @FXML
    private void editPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AllDashboard/editUser.fxml"));
            AnchorPane popup = loader.load();

            EditUserController controller = loader.getController();
            controller.setUsername(username);

            // Posisi popup di tengah
            popup.setLayoutX((popupLayer.getWidth() - popup.getPrefWidth()) / 2);
            popup.setLayoutY((popupLayer.getHeight() - popup.getPrefHeight()) / 2);

            // Tambahkan ke layer
            popupLayer.getChildren().setAll(popup);
            popupLayer.setVisible(true);
            popupLayer.setMouseTransparent(false);
            popupLayer.setPickOnBounds(true);
            popupLayer.getChildren().setAll(darkOverlay, popup);

            // Ambil scene setelah popup ditambahkan ke tampilan (dijamin sudah punya scene)
            if (popup.getScene() != null) {
                popup.getScene().setUserData(this); // INI akan pasti berhasil
            } else {
                // Jika scene belum tersedia, tunda sedikit
                popup.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        newScene.setUserData(this);
                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closePopup1() {
        popupLayer.setVisible(false);
        popupLayer.setMouseTransparent(true);   // LEPAS event agar elemen di bawah bisa diklik
        popupLayer.getChildren().clear();
    }



    @FXML
    public void switchToLogin() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi");
        alert.setHeaderText("Keluar dari akun?");
        alert.setContentText("Apakah Anda yakin ingin logout?");

        // Tetapkan owner agar tidak jadi popup "liar"
        alert.initOwner(pnlKeluar.getScene().getWindow());

        ButtonType buttonYes = new ButtonType("Ya");
        ButtonType buttonNo = new ButtonType("Tidak");

        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonYes) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/gojalan/Login.fxml"));
                    Parent root = fxmlLoader.load();

                    Stage stage = (Stage) pnlKeluar.getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setFullScreen(true); // pastikan root login tidak kosong
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setHeaderText("Gagal memuat halaman Login");
                    error.setContentText(e.getMessage());
                    error.showAndWait();
                }
            }
        });
    }







}
