package cz.zcu.kiv.nlp.ir.trec.gui;

import cz.zcu.kiv.nlp.ir.trec.Index.Index;
import cz.zcu.kiv.nlp.ir.trec.data.DocumentNew;
import cz.zcu.kiv.nlp.ir.trec.data.document.Document;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.util.ArrayList;
import java.util.List;

/**
 * Second view which is used for edit documents
 */
public class EditController
{

    private Index index;

    private List<Document> documents;

    @FXML
    private TableView<Document> tableView;

    @FXML
    private TableColumn columnId;

    @FXML
    private TableColumn columnTitle;

    @FXML
    private Label editId;

    @FXML
    private TextField idInput;

    @FXML
    private TextArea titleInput;

    @FXML
    private TextArea textInput;

    @FXML
    private Label statusLabel;

    private boolean sendUpdatedDocuments = false;

    /**
     * Sets data for window
     *
     * @param index     the index
     * @param documents the documents
     */
    public void setData(Index index, List<Document> documents)
    {
        this.index = index;
        this.documents = new ArrayList<>(documents);

        // fill data to table
        this.fillTable();
    }

    /**
     * Fill table.
     */
    public void fillTable()
    {
        columnId.setCellValueFactory(new PropertyValueFactory("id"));
        columnTitle.setCellValueFactory(new PropertyValueFactory("title"));

        this.tableView.setItems(getInitialTableData());
    }

    /**
     * Converts data from List to Observable list
     * @return observableList
     */
    private ObservableList getInitialTableData()
    {
        ObservableList data = FXCollections.observableList(this.documents);

        return data;
    }


    /**
     * Load main window.
     *
     * @param event the event
     */
    public void loadMainWindow(ActionEvent event)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("layout.fxml"));
            Parent pane = loader.load();

            // if documents are saved, send them back to main window
            if (this.sendUpdatedDocuments)
            {
                AppController appController = loader.getController();
                appController.setUpdatedDocuments(this.documents);
            }

            Scene secondScene = new Scene(pane);

            Window window = ((Node) event.getTarget()).getScene().getWindow();
            Stage stage = (Stage) window;
            stage.setScene(secondScene);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Loads selected item to inputs
     */
    @FXML
    private void showElement()
    {
        Document document = this.tableView.getSelectionModel().getSelectedItem();

        if (document == null)
        {
            return;
        }

        this.editId.setText(document.getId());
        this.idInput.setText(document.getId());
        this.titleInput.setText(document.getTitle());
        this.textInput.setText(document.getText());
    }

    /**
     * Handler for "Vytvořit" button
     * Adds element
     */
    @FXML
    private void addRecord()
    {
        String docId = this.idInput.getText();

        // check if id is not used
        if (this.getDocumentById(docId) != null)
        {
            this.statusLabel.setText("Dokument s ID: " + docId + " už existuje!");
            return;
        }

        DocumentNew documentNew = new DocumentNew();
        documentNew.setId(docId);
        documentNew.setText(this.textInput.getText());
        documentNew.setTitle(this.titleInput.getText());

        documents.add(documentNew);

        this.refreshTableView();

        this.statusLabel.setText("Dokument s ID: " + docId + " byl přidán");

    }

    /**
     * Handler for "Upravit" button
     * Updates selected document
     */
    @FXML
    private void updateRecord()
    {
        // load hidden id value of selected document
        String docId = this.editId.getText();

        DocumentNew document = (DocumentNew) this.getDocumentById(docId);

        if (document == null)
        {
            this.statusLabel.setText("Nelze upravit nový záznam. Je ho nutné nejdřív vložit");
            return;
        }

        String newDocId = this.idInput.getText();
        if (!docId.equals(newDocId))
        {
            if (this.getDocumentById(newDocId) != null)
            {
                this.statusLabel.setText("Dokument s ID: " + newDocId + " už existuje!");
                return;
            }
        }

        document.setId(newDocId);
        document.setText(this.textInput.getText());
        document.setTitle(this.titleInput.getText());

        this.refreshTableView();

        this.statusLabel.setText("Dokument s ID: " + newDocId + " byl upraven");
    }

    /**
     * Handler for "Vymazat" button
     * Removes selected document from documents
     */
    @FXML
    private void deleteRecord()
    {
        String docId = this.editId.getText();

        DocumentNew document = (DocumentNew) this.getDocumentById(docId);

        documents.remove(document);

        this.refreshTableView();

        this.statusLabel.setText("Dokument s ID: " + docId + " byl odstraněn");


    }

    /**
     * Save document and reindex index
     */
    @FXML
    private void save()
    {
        this.index.index(documents, this.index.getDataType());

        this.statusLabel.setText("Index byl přegenerován!");

        this.sendUpdatedDocuments = true;

    }


    /**
     * Returns document from documents by id
     * @param docId document id
     * @return document|null
     */
    private Document getDocumentById(String docId)
    {
        for (Document document : documents)
        {
            if (document.getId().equals(docId))
            {
                return document;
            }
        }

        return null;
    }

    /**
     * Little magic here
     * For refresh table view
     */
    private void refreshTableView()
    {
        tableView.getColumns().get(0).setVisible(false);
        tableView.getColumns().get(0).setVisible(true);
    }
}
