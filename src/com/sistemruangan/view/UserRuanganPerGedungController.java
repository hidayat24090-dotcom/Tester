package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.RuanganController;
import com.sistemruangan.model.Gedung;
import com.sistemruangan.model.Ruangan;
import com.sistemruangan.util.DialogUtil;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.File;

/**
 * Controller untuk Ruangan Per Gedung - User Side
 */
public class UserRuanganPerGedungController {
    
    @FXML private Label lblGedungNama;
    @FXML private Label lblGedungInfo;
    @FXML private Label lblTotal;
    @FXML private Label lblTersedia;
    @FXML private Label lblDipinjam;
    
    @FXML private ComboBox<String> cbFilterLantai;
    @FXML private ComboBox<String> cbFilterStatus;
    @FXML private TextField txtSearch;
    @FXML private FlowPane flowPaneRuangan;
    @FXML private Button btnRefresh;
    @FXML private Button btnKembali;
    @FXML private ProgressIndicator progressIndicator;
    
    private RuanganController ruanganController;
    private static Gedung selectedGedung;
    private FilteredList<Ruangan> filteredData;
    private static final String FOTO_DIR = "resources/images/ruangan/";
    private Image defaultImage;
    
    public static void setSelectedGedung(Gedung gedung) {
        selectedGedung = gedung;
    }
    
    @FXML
    public void initialize() {
        System.out.println("🔧 Initializing UserRuanganPerGedungController...");
        
        if (selectedGedung == null) {
            System.err.println("❌ No gedung selected!");
            MainApp.showUserGedungList();
            return;
        }
        
        ruanganController = new RuanganController();
        
        // Set gedung info
        lblGedungNama.setText(selectedGedung.getNamaGedung());
        
        // Setup filter lantai
        cbFilterLantai.getItems().add("Semua");
        for (int i = 1; i <= selectedGedung.getJumlahLantai(); i++) {
            cbFilterLantai.getItems().add("Lantai " + i);
        }
        cbFilterLantai.setValue("Semua");
        
        // Setup filter status
        cbFilterStatus.getItems().addAll("Semua", "Tersedia", "Dipinjam");
        cbFilterStatus.setValue("Semua");
        
        // Load default image
        loadDefaultImage();
        
        // Load data
        loadDataAsync();
    }
    
    private void loadDefaultImage() {
        try {
            File defaultFile = new File("resources/images/default_room.png");
            if (defaultFile.exists()) {
                defaultImage = new Image(defaultFile.toURI().toString());
            }
        } catch (Exception e) {
            defaultImage = null;
        }
    }
    
    private void loadDataAsync() {
        progressIndicator.setVisible(true);
        
        Task<ObservableList<Ruangan>> loadTask = new Task<ObservableList<Ruangan>>() {
            @Override
            protected ObservableList<Ruangan> call() throws Exception {
                return ruanganController.getRuanganByGedung(selectedGedung.getId());
            }
            
            @Override
            protected void succeeded() {
                ObservableList<Ruangan> ruanganList = getValue();
                filteredData = new FilteredList<>(ruanganList, p -> true);
                
                lblGedungInfo.setText(
                    selectedGedung.getJumlahLantai() + " Lantai • " + 
                    ruanganList.size() + " Ruangan"
                );
                
                displayRuanganCards();
                updateSummary();
                
                progressIndicator.setVisible(false);
                System.out.println("✅ Loaded " + ruanganList.size() + " ruangan");
            }
            
            @Override
            protected void failed() {
                progressIndicator.setVisible(false);
                DialogUtil.showDialog(
                    DialogUtil.DialogType.ERROR,
                    "Error",
                    "Gagal memuat data ruangan!",
                    MainApp.getRootContainer()
                );
            }
        };
        
        new Thread(loadTask).start();
    }
    
    private void displayRuanganCards() {
        flowPaneRuangan.getChildren().clear();
        
        if (filteredData.isEmpty()) {
            VBox noData = new VBox(10);
            noData.setAlignment(Pos.CENTER);
            noData.setPadding(new Insets(40));
            
            Label icon = new Label("📭");
            icon.setStyle("-fx-font-size: 48px;");
            
            Label text = new Label("Belum ada ruangan di gedung ini");
            text.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
            
            noData.getChildren().addAll(icon, text);
            flowPaneRuangan.getChildren().add(noData);
            return;
        }
        
        for (Ruangan ruangan : filteredData) {
          VBox card = createRuanganCard(ruangan);
          flowPaneRuangan.getChildren().add(card);
        }
    }

    private VBox createRuanganCard(Ruangan ruangan) {
      VBox card = new VBox(12);
      card.setPrefWidth(320);
      card.setStyle(
          "-fx-background-color: white;" +
          "-fx-background-radius: 12;" +
          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 12, 0, 0, 3);" +
          "-fx-padding: 0;"
      );
      
      // Hover effect
      card.setOnMouseEntered(e -> 
          card.setStyle(
              "-fx-background-color: white;" +
              "-fx-background-radius: 12;" +
              "-fx-effect: dropshadow(gaussian, rgba(91,155,213,0.3), 15, 0, 0, 5);" +
              "-fx-padding: 0;" +
              "-fx-cursor: hand;"
          )
      );
      
      card.setOnMouseExited(e -> 
          card.setStyle(
              "-fx-background-color: white;" +
              "-fx-background-radius: 12;" +
              "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 12, 0, 0, 3);" +
              "-fx-padding: 0;"
          )
      );
      
      // Image
      StackPane imageContainer = new StackPane();
      imageContainer.setPrefHeight(200);
      imageContainer.setStyle("-fx-background-color: #f5f7fa; -fx-background-radius: 12 12 0 0;");
      
      ImageView imageView = new ImageView();
      imageView.setFitWidth(320);
      imageView.setFitHeight(200);
      imageView.setPreserveRatio(false);
      imageView.setSmooth(true);
      imageView.setCache(true);
      
      loadRuanganImage(ruangan, imageView);
      imageContainer.getChildren().add(imageView);
      
      // Content
      VBox content = new VBox(10);
      content.setPadding(new Insets(15));
      
      Label lblNama = new Label(ruangan.getNamaRuangan());
      lblNama.setFont(Font.font("System", FontWeight.BOLD, 16));
      lblNama.setStyle("-fx-text-fill: #2c3e50;");
      lblNama.setWrapText(true);
      lblNama.setMaxWidth(280);
      
      HBox infoBox = new HBox(5);
      infoBox.setAlignment(Pos.CENTER_LEFT);
      Label iconLantai = new Label("📍");
      Label lblLantai = new Label("Lantai " + ruangan.getLantai() + " • " + ruangan.getJumlahKursi() + " Kursi");
      lblLantai.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");
      infoBox.getChildren().addAll(iconLantai, lblLantai);
      
      // Fasilitas
      HBox fasilitasBox = new HBox(5);
      fasilitasBox.setAlignment(Pos.CENTER_LEFT);
      Label iconFas = new Label("📋");
      
      String fasText = "Tidak ada fasilitas";
      if (ruangan.getFasilitas() != null && !ruangan.getFasilitas().trim().isEmpty()) {
          String[] fas = ruangan.getFasilitas().split(",");
          if (fas.length > 0) {
              fasText = fas[0].trim();
              if (fas.length > 1) {
                  fasText += ", " + fas[1].trim();
              }
              if (fas.length > 2) {
                  fasText += " +" + (fas.length - 2) + " lainnya";
              }
          }
      }
      
      Label lblFas = new Label(fasText);
      lblFas.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");
      lblFas.setWrapText(true);
      lblFas.setMaxWidth(250);
      fasilitasBox.getChildren().addAll(iconFas, lblFas);
      
      Label lblStatus = new Label(ruangan.getStatus().toUpperCase());
      lblStatus.setPadding(new Insets(4, 12, 4, 12));
      lblStatus.setStyle(
          "-fx-background-radius: 12;" +
          "-fx-font-size: 10px;" +
          "-fx-font-weight: bold;" +
          (ruangan.getStatus().equalsIgnoreCase("tersedia") 
              ? "-fx-background-color: #d4edda; -fx-text-fill: #155724;" 
              : "-fx-background-color: #fff3cd; -fx-text-fill: #856404;")
      );
      
      Button btnPinjam = new Button(
          ruangan.getStatus().equalsIgnoreCase("tersedia") 
              ? "📝 Ajukan" 
              : "❌ Tidak Tersedia"
      );
      btnPinjam.setMaxWidth(Double.MAX_VALUE);
      btnPinjam.setDisable(!ruangan.getStatus().equalsIgnoreCase("tersedia"));
      
      if (ruangan.getStatus().equalsIgnoreCase("tersedia")) {
          btnPinjam.setStyle(
              "-fx-background-color: #5B9BD5;" +
              "-fx-text-fill: white;" +
              "-fx-font-weight: bold;" +
              "-fx-font-size: 12px;" +
              "-fx-padding: 10 15;" +
              "-fx-background-radius: 6;" +
              "-fx-cursor: hand;"
          );
          
          btnPinjam.setOnMouseEntered(e -> 
              btnPinjam.setStyle(
                  "-fx-background-color: #4a8bc2;" +
                  "-fx-text-fill: white;" +
                  "-fx-font-weight: bold;" +
                  "-fx-font-size: 12px;" +
                  "-fx-padding: 10 15;" +
                  "-fx-background-radius: 6;" +
                  "-fx-cursor: hand;"
              )
          );
          
          btnPinjam.setOnMouseExited(e -> 
              btnPinjam.setStyle(
                  "-fx-background-color: #5B9BD5;" +
                  "-fx-text-fill: white;" +
                  "-fx-font-weight: bold;" +
                  "-fx-font-size: 12px;" +
                  "-fx-padding: 10 15;" +
                  "-fx-background-radius: 6;" +
                  "-fx-cursor: hand;"
              )
          );
      } else {
          btnPinjam.setStyle(
              "-fx-background-color: #e8edf2;" +
              "-fx-text-fill: #95a5a6;" +
              "-fx-font-weight: bold;" +
              "-fx-font-size: 12px;" +
              "-fx-padding: 10 15;" +
              "-fx-background-radius: 6;"
          );
      }
      
      btnPinjam.setOnAction(e -> handlePinjam(ruangan));
      
      content.getChildren().addAll(
          lblNama,
          new Separator(),
          infoBox,
          fasilitasBox,
          lblStatus,
          btnPinjam
      );
      
      card.getChildren().addAll(imageContainer, content);
      
      return card;
  }

  private void loadRuanganImage(Ruangan ruangan, ImageView imageView) {
      try {
          if (ruangan.getFotoPath() != null && !ruangan.getFotoPath().isEmpty()) {
              File fotoFile = new File(FOTO_DIR + ruangan.getFotoPath());
              if (fotoFile.exists()) {
                  imageView.setImage(new Image(fotoFile.toURI().toString(), 320, 200, true, true, true));
                  return;
              }
          }
      } catch (Exception e) {
          System.err.println("⚠️ Error loading image: " + e.getMessage());
      }
      
      imageView.setImage(defaultImage);
  }

  private void handlePinjam(Ruangan ruangan) {
      DialogUtil.showConfirmation(
          "Konfirmasi Peminjaman",
          "Anda akan mengajukan peminjaman untuk ruangan: " + ruangan.getNamaRuangan() + 
          "\n\nAnda akan diarahkan ke form peminjaman. Lanjutkan?",
          MainApp.getRootContainer(),
          () -> {
              UserPeminjamanFormController.setSelectedRuangan(ruangan);
              MainApp.showUserPeminjamanForm();
          },
          null
      );
  }

  private void updateSummary() {
      int total = filteredData.size();
      int tersedia = 0;
      int dipinjam = 0;
      
      for (Ruangan r : filteredData) {
          if ("tersedia".equalsIgnoreCase(r.getStatus())) {
              tersedia++;
          } else {
              dipinjam++;
          }
      }
      
      lblTotal.setText(String.valueOf(total));
      lblTersedia.setText(String.valueOf(tersedia));
      lblDipinjam.setText(String.valueOf(dipinjam));
  }

  @FXML
  private void handleFilter() {
      String selectedLantai = cbFilterLantai.getValue();
      String selectedStatus = cbFilterStatus.getValue();
      String keyword = txtSearch.getText().trim().toLowerCase();
      
      filteredData.setPredicate(ruangan -> {
          boolean matchSearch = keyword.isEmpty() || 
              ruangan.getNamaRuangan().toLowerCase().contains(keyword);
          
          boolean matchLantai = "Semua".equals(selectedLantai) || 
              selectedLantai.equals("Lantai " + ruangan.getLantai());
          
          boolean matchStatus = "Semua".equals(selectedStatus) || 
              ruangan.getStatus().equalsIgnoreCase(selectedStatus);
          
          return matchSearch && matchLantai && matchStatus;
      });
      
      displayRuanganCards();
      updateSummary();
  }

  @FXML
  private void handleSearch() {
      handleFilter();
  }

  @FXML
  private void handleRefresh() {
      txtSearch.clear();
      cbFilterLantai.setValue("Semua");
      cbFilterStatus.setValue("Semua");
      loadDataAsync();
  }

  @FXML
  private void handleKembali() {
      MainApp.showUserGedungList();
  }
}