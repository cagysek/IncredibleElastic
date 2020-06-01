package cz.zcu.kiv.nlp.ir.trec.gui;

import cz.zcu.kiv.nlp.ir.trec.Index.Index;
import cz.zcu.kiv.nlp.ir.trec.Index.loader.LoaderFactory;
import cz.zcu.kiv.nlp.ir.trec.Index.preprocesing.Preprocessing;
import cz.zcu.kiv.nlp.ir.trec.Index.preprocesing.stemmer.CzechStemmerLight;
import cz.zcu.kiv.nlp.ir.trec.Index.preprocesing.tokenizer.AdvancedTokenizer;
import cz.zcu.kiv.nlp.ir.trec.config.Config;
import cz.zcu.kiv.nlp.ir.trec.data.document.Document;
import cz.zcu.kiv.nlp.ir.trec.data.enums.ELoaderType;
import cz.zcu.kiv.nlp.ir.trec.data.enums.ESearcherType;
import cz.zcu.kiv.nlp.ir.trec.data.result.Result;
import cz.zcu.kiv.nlp.ir.trec.search.searcher.Searcher;
import cz.zcu.kiv.nlp.ir.trec.search.searcher.SearcherFactory;
import cz.zcu.kiv.nlp.ir.trec.utils.IOUtils;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.io.IOException;
import java.util.List;

public class AppController extends Application
{

    @FXML
    private TextField searchInput;

    @FXML
    private Label infoLabel;

    @FXML
    private ListView<String> resultsView;

    @FXML
    private ToggleGroup typeSelect;

    @FXML
    private ToggleGroup dataSelect;

    @FXML
    private TextArea documentDetail;

    @FXML
    private RadioButton testDataRadio;

    @FXML
    private RadioButton ownDataRadio;

    @FXML
    private RadioButton czechDataRadio;

    private ELoaderType loadedData;

    private Index index;

    private Preprocessing preprocessing;

    private List<Document> documents;

    private boolean documentsUpdated = false;


    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException
    {
        FXMLLoader firstPaneLoader = new FXMLLoader(getClass().getClassLoader().getResource("layout.fxml"));
        Parent root = firstPaneLoader.load();

        Scene firstScene = new Scene(root);

        primaryStage.setScene(firstScene);

        primaryStage.show();

    }

    @FXML
    public void initialize()
    {
        this.infoLabel.setText("Načítám data...");

        this.preprocessing = new Preprocessing(new CzechStemmerLight(),
                new AdvancedTokenizer(),
                IOUtils.getStopWords(),
                false,
                true,
                true,
                true
        );

        this.index = new Index(preprocessing);

        if (!IOUtils.isIndexExists())
        {
            System.out.println("Index neexistuje, vytvářím nový...");

            this.createNewIndex();
        }
        else
        {
            System.out.println("Index existuje, načítám ho...");

            try
            {
                this.index.loadIndex();

                this.setUpDataRadioButton();

                if (this.index.getDataType() != null && !documentsUpdated)
                {
                    this.documents = new LoaderFactory().getLoader(this.index.getDataType()).loadDocuments();
                }
            }
            catch (Exception e)
            {
                System.out.println("Nepodařilo se nahrát uložený index. Vytvářím nový..");

                this.createNewIndex();
            }

        }

        this.infoLabel.setText("Připraven");

    }

    private void createNewIndex()
    {
        this.documents = new LoaderFactory().getLoader(Config.getDefaultLoader).loadDocuments();

        this.index.index(this.documents, Config.getDefaultLoader);

        this.setUpDataRadioButton();
    }

    private void setUpDataRadioButton()
    {
        if (this.index.getDataType() == ELoaderType.CZECH)
        {
            this.czechDataRadio.setSelected(true);
        }
        else if (this.index.getDataType() == ELoaderType.ARTICLE)
        {
            this.ownDataRadio.setSelected(true);
        }
        else if (this.index.getDataType() == ELoaderType.TEST)
        {
            this.testDataRadio.setSelected(true);
        }
    }

    @FXML
    private void search()
    {
        this.infoLabel.setText("Vyhledávám...");

        RadioButton selectedSearchType = (RadioButton) this.typeSelect.getSelectedToggle();
        RadioButton selectedDataType = (RadioButton) this.dataSelect.getSelectedToggle();

        ELoaderType dataType = ELoaderType.getDataTypeById(selectedDataType.getId());

        // zkontroluje jestli se nepřepnuly data, pokud ano udělá novej index
        this.checkTypeOfIndexedItems(dataType);

        Searcher searcher = new SearcherFactory().getSearcher(ESearcherType.getSearchTypeByText(selectedSearchType.getText()), this.preprocessing, this.index.getInvertedIndex());
        searcher.setResultCount(10);

        List<Result> results = searcher.search(searchInput.getText());

        ObservableList<String> items = FXCollections.observableArrayList();

        for(Result result : results)
        {
            items.add(result.getDocumentID());
        }

        this.resultsView.setItems(items);

        this.infoLabel.setText("Nalezeno výsledků: " + searcher.getTotalResultCount());
    }



    @FXML
    private void showItemInfo()
    {
        String selectedItem = this.resultsView.getSelectionModel().getSelectedItem();

        if (selectedItem == null)
        {
            return;
        }

        for (Document document : this.documents)
        {
            if (document.getId().equals(selectedItem))
            {
                this.documentDetail.setText(document.getTitle() + "\n" + "\n" + document.getText());
                break;
            }
        }
    }

    private void checkTypeOfIndexedItems(ELoaderType type)
    {
        if (type == this.index.getDataType())
        {
            return;
        }

        System.out.println("Byl změněn typ dat - načítám nová data");

        this.documents = new LoaderFactory().getLoader(type).loadDocuments();


        this.index.index(this.documents, type);

    }

    @FXML
    public void openSecondScene(ActionEvent actionEvent) {

        try
        {
            FXMLLoader editLoader = new FXMLLoader(getClass().getClassLoader().getResource("edit.fxml"));
            Parent secondPane = editLoader.load();
            EditController editController = editLoader.getController();
            editController.setData(this.index, this.documents);
            Scene secondScene = new Scene(secondPane);

            Window window = ((Node) actionEvent.getTarget()).getScene().getWindow();
            Stage stage = (Stage) window;
            stage.setScene(secondScene);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void setUpdatedDocuments(List<Document> documents)
    {
        this.documents = documents;
        this.documentsUpdated = true;
    }
}
