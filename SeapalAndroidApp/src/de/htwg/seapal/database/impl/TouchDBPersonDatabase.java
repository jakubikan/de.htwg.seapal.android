package de.htwg.seapal.database.impl;

import android.content.Context;
import android.util.Log;

import com.google.inject.Inject;

import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.UpdateConflictException;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.ViewResult.Row;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import de.htwg.seapal.database.IPersonDatabase;
import de.htwg.seapal.model.IPerson;
import de.htwg.seapal.model.ModelDocument;
import de.htwg.seapal.model.impl.Person;
import roboguice.inject.ContextSingleton;

@ContextSingleton
public class TouchDBPersonDatabase implements IPersonDatabase {
	private static final String TAG = "person-TouchDB";
	private static final String DATABASE_NAME = "seapal_person_db";

	private static TouchDBPersonDatabase TouchDBPersonDatabase;
	private final CouchDbConnector couchDbConnector;
	private final TouchDBHelper dbHelper;

	@Inject
	public TouchDBPersonDatabase(Context ctx) {
		dbHelper = new TouchDBHelper(DATABASE_NAME);
		dbHelper.createDatabase(ctx);
		dbHelper.pullFromDatabase();
		couchDbConnector = dbHelper.getCouchDbConnector();
	}

	public static TouchDBPersonDatabase getInstance(Context ctx) {
		if (TouchDBPersonDatabase == null)
			TouchDBPersonDatabase = new TouchDBPersonDatabase(ctx);
		return TouchDBPersonDatabase;
	}

	@Override
	public boolean open() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public UUID create() {
		IPerson person = new Person();
		try {
			couchDbConnector.create(person.getId(), person);
		} catch (UpdateConflictException e) {
			Log.e(TAG, e.toString());
		}
		UUID id = UUID.fromString(person.getId());
		Log.d(TAG, "person created: " + person.getId());
		dbHelper.pushToDatabase();
		return id;
	}

	@Override
	public boolean save(IPerson data) {
		try {
			couchDbConnector.update(data);
			dbHelper.pushToDatabase();
		} catch (DocumentNotFoundException e) {
			Log.d(TAG, "Document not Found");
			Log.d(TAG, e.toString());
			return false;
		}
		Log.d(TAG, "Person saved: " + data.getId());
		return true;
	}

	@Override
	public IPerson get(UUID id) {
		IPerson person;
		try {
			person = couchDbConnector.get(Person.class, id.toString());
		} catch (DocumentNotFoundException e) {
			Log.e(TAG, "person not found" + id.toString());
			return null;
		}
		return person;
	}

	@Override
	public List<IPerson> loadAll() {
		List<IPerson> lst = new LinkedList<IPerson>();
		List<String> log = new LinkedList<String>();
		ViewQuery query = new ViewQuery().allDocs();
		ViewResult vr = couchDbConnector.queryView(query);

		for (Row r : vr.getRows()) {
			Log.d(TAG, "All Persons: " + r.getId());
			lst.add(get(UUID.fromString(r.getId())));
			log.add(r.getId());
		}
		Log.d(TAG, "All Persons: " + log.toString());
		return lst;
	}

	@Override
	public void delete(UUID id) {
		try {
			couchDbConnector.delete(get(id));
			dbHelper.pushToDatabase();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return;
		}
		Log.d(TAG, "Person deleted");
	}

	@Override
	public boolean close() {
		// TODO Auto-generated method stub
		return false;
	}

    @Override
    public void create(ModelDocument modelDocument) {

    }

    @Override
    public List<? extends IPerson> queryViews(String s, String s2) {
        return null;
    }

    @Override
    public void update(ModelDocument modelDocument) {

    }

}
