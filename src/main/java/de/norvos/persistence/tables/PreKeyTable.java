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
package de.norvos.persistence.tables;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.whispersystems.libaxolotl.state.PreKeyRecord;

import de.norvos.persistence.Database;

public class PreKeyTable implements Table {
	private static PreKeyTable instance;

	synchronized public static PreKeyTable getInstance() {
		if (instance == null) {
			instance = new PreKeyTable();
		}
		return instance;
	}

	private PreKeyTable() {
	}

	public void deleteKey(final int id) throws SQLException {
		final String query = "DELETE FROM prekeystore WHERE id = ?";

		try (PreparedStatement stmt = Database.ensureTableExists(this).prepareStatement(query)) {

			stmt.setInt(1, id);

			stmt.execute();
		}
	}

	@Override
	public String getCreationStatement() {
		return "CREATE TABLE IF NOT EXISTS prekeystore ( id INTEGER PRIMARY KEY, prekey BINARY NOT NULL)";
	}

	public PreKeyRecord getKey(final int id) throws SQLException {
		final String query = "SELECT prekey FROM prekeystore WHERE id = ?";

		try (PreparedStatement stmt = Database.ensureTableExists(this).prepareStatement(query)) {

			stmt.setInt(1, id);
			final ResultSet result = stmt.executeQuery();
			if (result.first()) {
				try {
					return new PreKeyRecord(result.getBytes(1));
				} catch (final IOException e) {
					throw new SQLException("PreKeyTable: Value of prekey for id [" + id + "] is invalid.", e);
				}
			} else {
				return null;
			}
		}
	}

	public int getLastIndex() {
		// TODO implement circular buffer for PreKey generation
		//
		// From discussion with Moxie in June 2015:
		//
		// Connor:
		// As the keys are only recreated when the
		// number of them stored on the server runs low and thus there are no
		// keys
		// that could collide with the new ones, is it safe to assume that a
		// client can always use zero as the starting ID?
		//
		// Moxie:
		// No, someone could have requested a prekey but not delivered a message
		// with it yet. You should pick a random starting index, then treat it
		// as
		// a circular buffer.

		return 0;
	}

	public void storeKey(final int id, final PreKeyRecord preKey) throws SQLException {
		final String query = "MERGE INTO prekeystore VALUES (?, ?)";

		try (PreparedStatement stmt = Database.ensureTableExists(this).prepareStatement(query)) {

			stmt.setInt(1, id);
			stmt.setBytes(2, preKey.serialize());

			stmt.execute();
		}
	}
}
