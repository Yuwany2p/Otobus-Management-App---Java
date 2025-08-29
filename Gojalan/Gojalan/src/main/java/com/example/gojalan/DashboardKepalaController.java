    package com.example.gojalan;

    import Laporan.LaporanController;
    import Master.Setting.settingCRUDController;
    import Master.User.userCRUDController;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.Alert;
    import javafx.scene.control.ButtonType;
    import javafx.scene.control.Label;
    import javafx.scene.control.PasswordField;
    import javafx.scene.image.ImageView;
    import javafx.scene.layout.AnchorPane;
    import javafx.scene.layout.Pane;
    import javafx.stage.Modality;
    import javafx.stage.Stage;

    import java.io.IOException;

    public class DashboardKepalaController {

        @FXML
        private AnchorPane pnlBtnHalamanAwal;

        @FXML
        private Label lbNama;

        @FXML
        private Label lbAtas;

        @FXML
        private Pane PanelHalamanAwal;

        @FXML
        private AnchorPane pnlContent;

        @FXML
        private Pane PanelKeluar;

        @FXML
        private Pane panelPenanda;

        @FXML
        private Pane PanelUser;

        @FXML
        private Pane PanelTabelSetting;

        @FXML
        private Pane PanelLaporan;

        @FXML
        private Label lblEdit;

        @FXML
        private AnchorPane popupLayer;

        @FXML
        private Label lblKeluar;

        @FXML
        private Pane darkOverlay;

        @FXML
        private ImageView imgKeluar;

        public String username;
        public String nama;

        public String getUsername() {
            return username;
        }

        public void setData(String nama, String usr) {
            username=usr;
            this.nama = nama;
            lbNama.setText(nama);
        }

        public void loadPage(String fxmlPath) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                AnchorPane pane = loader.load();
                Object controller = loader.getController();

                if (controller instanceof userCRUDController) {
                    userCRUDController usr = (userCRUDController) controller;
                    usr.setUsername(username);
                }

                if (controller instanceof settingCRUDController) {
                    settingCRUDController stg = (settingCRUDController) controller;
                    stg.setUsername(username);
                }

                if (controller instanceof LaporanController) {
                    LaporanController lpr = (LaporanController) controller;
                    lpr.setNama(username, nama);
                }

                pnlContent.getChildren().setAll(pane);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @FXML
        private void handleHalamanAwal() {
            lbAtas.setText("Halaman Awal");
            loadPage("/com/example/gojalan/KepalaTravel/HalamanAwal.fxml");
            panelPenanda.setLayoutY(PanelHalamanAwal.getLayoutY());
        }

        @FXML
        private void handleUser() {
            lbAtas.setText("User");
            loadPage("/com/example/gojalan/KepalaTravel/User.fxml");
            panelPenanda.setLayoutY(PanelUser.getLayoutY());
        }

        @FXML
        private void handleTabelSetting() {
            System.out.println("coba :  "+ username);
            lbAtas.setText("Tabel Setting");
            loadPage("/com/example/gojalan/KepalaTravel/TabelSetting.fxml");
            panelPenanda.setLayoutY(PanelTabelSetting.getLayoutY());
        }


        @FXML
        private void handleLaporan() {
            lbAtas.setText("Laporan");
            loadPage("/com/example/gojalan/KepalaTravel/Laporan.fxml");
            panelPenanda.setLayoutY(PanelLaporan.getLayoutY());
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
            alert.initOwner(PanelKeluar.getScene().getWindow());

            ButtonType buttonYes = new ButtonType("Ya");
            ButtonType buttonNo = new ButtonType("Tidak");

            alert.getButtonTypes().setAll(buttonYes, buttonNo);

            alert.showAndWait().ifPresent(response -> {
                if (response == buttonYes) {
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/gojalan/Login.fxml"));
                        Parent root = fxmlLoader.load();

                        Stage stage = (Stage) PanelKeluar.getScene().getWindow();
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
