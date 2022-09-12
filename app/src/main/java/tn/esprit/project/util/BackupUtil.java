/*
 * This file is part of TrackWorkTime (TWT).
 *
 * TWT is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License 3.0 as published by
 * the Free Software Foundation.
 *
 * TWT is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License 3.0 for more details.
 *
 * You should have received a copy of the GNU General Public License 3.0
 * along with TWT. If not, see <http://www.gnu.org/licenses/>.
 */
package tn.esprit.project.util;

import static org.zephyrsoft.trackworktime.DocumentTreeStorage.exists;

import android.app.Activity;
import android.content.SharedPreferences;

import org.pmw.tinylog.Logger;
import org.zephyrsoft.trackworktime.Basics;
import org.zephyrsoft.trackworktime.DocumentTreeStorage;
import org.zephyrsoft.trackworktime.backup.BackupFileInfo;
import org.zephyrsoft.trackworktime.database.DAO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class BackupUtil {

    private BackupUtil() {
        // only static usage
    }

    public static Boolean doBackup(Activity activity, BackupFileInfo info) {
        try {
            DAO dao = Basics.get(activity).getDao();
            SharedPreferences preferences = Basics.get(activity).getPreferences();

            DocumentTreeStorage.writing(activity, info.getType(), info.getPreferencesBackupFile(), outputStream -> {
                try (Writer writer = new OutputStreamWriter(outputStream);
                     BufferedWriter output = new BufferedWriter(writer)) {
                    PreferencesUtil.writePreferences(preferences, output);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            DocumentTreeStorage.writing(activity, info.getType(), info.getEventsBackupFile(), outputStream -> {
                try (Writer writer = new OutputStreamWriter(outputStream);
                     BufferedWriter output = new BufferedWriter(writer)) {
                    dao.backupEventsToWriter(output);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            DocumentTreeStorage.writing(activity, info.getType(), info.getTargetsBackupFile(), outputStream -> {
                try (Writer writer = new OutputStreamWriter(outputStream);
                     BufferedWriter output = new BufferedWriter(writer)) {
                    dao.backupTargetsToWriter(output);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            return true;
        } catch (Exception e) {
            Logger.warn(e, "problem while writing backup");
            return false;
        }
    }

    public static Boolean doRestore(Activity activity, BackupFileInfo info) {
        try {
            DAO dao = Basics.get(activity).getDao();
            SharedPreferences preferences = Basics.get(activity).getPreferences();

            if (exists(activity, info.getType(), info.getPreferencesBackupFile())) {
                DocumentTreeStorage.reading(activity, info.getType(), info.getPreferencesBackupFile(),
                    reader -> {
                        try (final BufferedReader input = new BufferedReader(reader)) {
                            PreferencesUtil.readPreferences(preferences, input);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            }

            if (exists(activity, info.getType(), info.getEventsBackupFile())) {
                DocumentTreeStorage.reading(activity, info.getType(), info.getEventsBackupFile(),
                    reader -> {
                        try (final BufferedReader input = new BufferedReader(reader)) {
                            dao.restoreEventsFromReader(input);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            }

            if (exists(activity, info.getType(), info.getTargetsBackupFile())) {
                DocumentTreeStorage.reading(activity, info.getType(), info.getTargetsBackupFile(),
                    reader -> {
                        try (final BufferedReader input = new BufferedReader(reader)) {
                            dao.restoreTargetsFromReader(input);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            }
            return true;
        } catch (Exception e) {
            Logger.warn(e, "problem while restoring backup");
            return false;
        }
    }

}
