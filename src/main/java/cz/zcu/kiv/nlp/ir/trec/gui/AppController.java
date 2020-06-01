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
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main window of application
 */
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

    private Index index;

    private Preprocessing preprocessing;

    private List<Document> documents;

    private boolean documentsUpdated = false;


    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
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

    /**
     * Initialize.
     */
    @FXML
    public void initialize()
    {
        this.infoLabel.setText("Načítám data...");

        // set up preprocessing
        this.preprocessing = new Preprocessing(new CzechStemmerLight(),
                new AdvancedTokenizer(),
                IOUtils.getStopWords(),
                false,
                true,
                true,
                true
        );

        // set up index
        this.index = new Index(preprocessing);

        // checks if index file exists
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
                // try load index
                this.index.loadIndex();

                // mark radiobutton based on data in index
                this.setUpDataRadioButton();

                // checks loads data from file, depends data type, if documents are updated (from edit window), data are not loaded
                if (this.index.getDataType() != null && !documentsUpdated)
                {
                    this.documents = new LoaderFactory().getLoader(this.index.getDataType()).loadDocuments();
                }
            }
            catch (Exception e)
            {
                // if something is wrong with stored data, new index is created
                System.out.println("Nepodařilo se nahrát uložený index. Vytvářím nový..");

                this.createNewIndex();
            }

        }

        this.infoLabel.setText("Připraven");

    }

    /**
     * Creates new index
     */
    private void createNewIndex()
    {
        // load default data
        this.documents = new LoaderFactory().getLoader(Config.getDefaultLoader).loadDocuments();

        // index them
        this.index.index(this.documents, Config.getDefaultLoader);

        // mark radio button for selected data
        this.setUpDataRadioButton();
    }

    /**
     * Marks right radiobutton depends on loaded data
     */
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

    /**
     * Search method, handler for button "Hledat"
     */
    @FXML
    private void search()
    {
        this.infoLabel.setText("Vyhledávám...");

        // loads selected search algorithm and data
        RadioButton selectedSearchType = (RadioButton) this.typeSelect.getSelectedToggle();
        RadioButton selectedDataType = (RadioButton) this.dataSelect.getSelectedToggle();

        ELoaderType dataType = ELoaderType.getDataTypeById(selectedDataType.getId());

        // checks loaded data vs selected. If values is not same, loads right data
        this.checkTypeOfIndexedItems(dataType);

        // set up searches
        Searcher searcher = new SearcherFactory().getSearcher(ESearcherType.getSearchTypeByText(selectedSearchType.getText()),
                                                                this.preprocessing,
                                                                this.index.getInvertedIndex());
        // limit result
        searcher.setResultCount(10);

        List<Result> results = new ArrayList<>();

        try
        {
            // search given query
            results = searcher.search(searchInput.getText());
        }
        catch (QueryNodeException e)
        {
            this.infoLabel.setText("Query není validní. Zkotrolujte jí!");
            return;
        }
        catch (Exception e)
        {
            this.infoLabel.setText("Ops");
            e.printStackTrace();
            return;
        }

        // convert data for view and shows them
        ObservableList<String> items = FXCollections.observableArrayList();

        for(Result result : results)
        {
            items.add(result.getDocumentID());
        }

        this.resultsView.setItems(items);

        this.infoLabel.setText("Nalezeno výsledků: " + searcher.getTotalResultCount());
    }


    /**
     * Shows detail info about document
     * Handler for list view item
     */
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

    /**
     * Compare given loader type with type in index
     * if values are not same loads right data
     * @param type
     */
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

    /**
     * Open second scene for edit documents
     *
     * @param actionEvent the action event
     */
    @FXML
    public void openSecondScene(ActionEvent actionEvent) {

        try
        {
            FXMLLoader editLoader = new FXMLLoader(getClass().getClassLoader().getResource("edit.fxml"));
            Parent secondPane = editLoader.load();
            EditController editController = editLoader.getController();

            // set data for edit scene
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

    /**
     * Sets updated documents from edit window
     *
     * @param documents the documents
     */
    public void setUpdatedDocuments(List<Document> documents)
    {
        this.documents = documents;
        this.documentsUpdated = true;
    }
}
