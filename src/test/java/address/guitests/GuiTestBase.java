package address.guitests;

import address.MainApp;
import address.TestApp;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.loadui.testfx.GuiTest;
import org.testfx.api.FxToolkit;
import org.testfx.service.query.NodeQuery;

import java.util.concurrent.TimeoutException;

public class GuiTestBase extends GuiTest {
    Stage stage;

    @Override
    protected Parent getRootNode() {
        Parent root = stage.getScene().getRoot();
        stage.getScene().setRoot(new Group());
        return root;
    }

    public GuiTestBase() {
        try {
            FXMLLoader.setDefaultClassLoader(TestApp.class.getClassLoader()); // workaround to fxml loading problems
            FxToolkit.registerPrimaryStage();
            FxToolkit.setupApplication(TestApp.class);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        stage = FxToolkit.toolkitContext().getRegisteredStage();
    }

    protected NodeQuery getWindowNode(String windowTitleRegex) {
        return from(rootNode(window(windowTitleRegex)));
    }
}
