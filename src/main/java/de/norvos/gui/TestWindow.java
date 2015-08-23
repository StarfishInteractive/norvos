/*******************************************************************************
 * Copyright (C) 2015 Connor Lanigan (email: dev@connorlanigan.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.norvos.gui;

import java.io.IOException;
import java.net.URL;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.whispersystems.libaxolotl.logging.AxolotlLoggerProvider;

import de.norvos.account.AccountDataStore;
import de.norvos.utils.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestWindow extends Application {

	public static void main(final String[] args) {
		Security.addProvider(new BouncyCastleProvider());

		AxolotlLoggerProvider.setProvider(
				(priority, tag, message) -> System.err.println("Priority " + priority + ": [" + tag + "] " + message));
		launch(args);
	}

	private Stage primaryStage;

	private void initializeDB() {
		AccountDataStore.storeStringValue("url", "https://textsecure-service.whispersystems.org");
	}

	/**
	 * Loads an FXML file into the main window.
	 *
	 * @param fxml
	 *            the path to the FXML file
	 * @return the controller associated with the FXML
	 * @throws IOException
	 *             if an error occurs during loading
	 */
	public <T> T loadFXML(final String fxml) {
		final URL fxmlURL = getClass().getResource(Constants.FXML_LOCATION + fxml);
		final FXMLLoader loader = new FXMLLoader(fxmlURL);
		Parent parent;
		try {
			parent = loader.load();
			final T controller = loader.<T> getController();
			final Scene scene = new Scene(parent, 1060, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			return controller;
		} catch (final IOException e) {
			// TODO logging
			// System.err.println("FXML could not be loaded: [" + fxml + "]");
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}

	@Override
	public void start(final Stage primaryStage) {
		this.primaryStage = primaryStage;
		primaryStage.setTitle(Constants.WINDOW_TITLE);
		primaryStage.centerOnScreen();

		initializeDB();

		loadFXML("Overview.fxml");
	}

}