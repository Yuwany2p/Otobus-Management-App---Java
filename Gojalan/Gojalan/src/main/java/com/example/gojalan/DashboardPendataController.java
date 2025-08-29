package com.example.gojalan;

import Master.Kursi.kursiCRUDController;
import Master.Rute.ruteCRUDController;
import Master.Supir.supirCRUDController;
import Master.Bus.busCRUDController;
import Master.Tiket.TiketCRUDController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardPendataController {
    @FXML
    private Label LbName;

    @FXML
    private Label LbAtas;

    @FXML
    private AnchorPane pnlContent;

    @FXML
    private Pane pnlBtnKeluar;

    @FXML
    private Pane panelPenanda;

    @FXML
    private Pane pnlBtnRute;

    @FXML
    private Pane pnlBtnKursi;

    @FXML
    private Pane pnlBtnSupir;

    @FXML
    private Pane pnlBtnTiket;

    @FXML
    private Pane pnlBtnBus;

    @FXML
    private Pane pnlBtnHalamanAwal;

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


    String username= "";
    public String getUsername() {
        return username;
    }

    public void setData(String nama, String usr) {
        username=usr;
        LbName.setText(nama);
    }

    public void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            AnchorPane pane = loader.load();
            Object controller = loader.getController();

            if (controller instanceof kursiCRUDController) {
                kursiCRUDController krs = (kursiCRUDController) controller;
                krs.setUsername(username);
            }

            if (controller instanceof supirCRUDController) {
                supirCRUDController spr = (supirCRUDController) controller;
                spr.setUsername(username);
            }

            if (controller instanceof busCRUDController) {
                busCRUDController bus = (busCRUDController) controller;
                bus.setUsername(username);
            }

            if (controller instanceof ruteCRUDController) {
                ruteCRUDController rute = (ruteCRUDController) controller;
                rute.setUsername(username);
            }

            if (controller instanceof TiketCRUDController) {
                TiketCRUDController tiket = (TiketCRUDController) controller;
                tiket.setUsername(username);
            }

            pnlContent.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHalamanAwal() {
        LbAtas.setText("HALAMAN AWAL");
        loadPage("/com/example/gojalan/PendataBus/HalamanAwal.fxml");
        panelPenanda.setLayoutY(pnlBtnHalamanAwal.getLayoutY());
    }

    @FXML
    private void handleRute() {
        LbAtas.setText("RUTE");
        loadPage("/com/example/gojalan/PendataBus/Rute.fxml");
        panelPenanda.setLayoutY(pnlBtnRute.getLayoutY());
    }

    @FXML
    private void handleBus() {
        LbAtas.setText("BUS");
        loadPage("/com/example/gojalan/PendataBus/Bus.fxml");
        panelPenanda.setLayoutY(pnlBtnBus.getLayoutY());
    }

    @FXML
    private void handleKursi() {
        LbAtas.setText("KURSI");
        loadPage("/com/example/gojalan/PendataBus/Kursi.fxml");
        panelPenanda.setLayoutY(pnlBtnKursi.getLayoutY());
    }

    @FXML
    private void handleSupir() {
        LbAtas.setText("SUPIR");
        loadPage("/com/example/gojalan/PendataBus/Supir.fxml");
        panelPenanda.setLayoutY(pnlBtnSupir.getLayoutY());
    }

    @FXML
    private void handleTiket() {
        LbAtas.setText("TIKET");
        loadPage("/com/example/gojalan/PendataBus/Tiket.fxml");
        panelPenanda.setLayoutY(pnlBtnTiket.getLayoutY());
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
        alert.initOwner(pnlBtnKeluar.getScene().getWindow());

        ButtonType buttonYes = new ButtonType("Ya");
        ButtonType buttonNo = new ButtonType("Tidak");

        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonYes) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/gojalan/Login.fxml"));
                    Parent root = fxmlLoader.load();

                    Stage stage = (Stage) pnlBtnKeluar.getScene().getWindow();
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
