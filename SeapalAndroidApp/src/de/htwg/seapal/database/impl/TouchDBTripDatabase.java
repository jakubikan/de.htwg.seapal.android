package de.htwg.seapal.database.impl;

import android.content.Context;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.ektorp.support.Views;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import de.htwg.seapal.database.ITripDatabase;
import de.htwg.seapal.model.ITrip;
import de.htwg.seapal.model.ModelDocument;
import de.htwg.seapal.model.impl.Trip;
import roboguice.inject.ContextSingleton;

@Views({
        @View(name = "singleDocument", map = "views/singleDocument.js"),
        @View(name = "own", map = "views/own.js"),
        @View(name = "boat", map = "views/trip/boat.js")
})
@ContextSingleton
public class TouchDBTripDatabase extends CouchDbRepositorySupport<Trip> implements ITripDatabase {

    private static final String TAG = "Trip-TouchDB";


    private final CouchDbConnector connector;
    private final TouchDBHelper dbHelper;

    @Inject
    public TouchDBTripDatabase(@Named("tripCouchDbConnector") TouchDBHelper helper, Context ctx) {
        super(Trip.class, helper.getCouchDbConnector());
        super.initStandardDesignDocument();
        dbHelper = helper;
        connector = dbHelper.getCouchDbConnector();
        Log.i(TAG, "Doc Ids " + super.getDesignDocumentFactory().generateFrom(this).getViews());


    }

    @Override
    public boolean open() {
        return true;
    }

    @Override
    public UUID create() {
        return null;
    }

    @Override
    public boolean save(ITrip data) {
        Trip entity = (Trip) data;

        if (entity.isNew()) {
            // ensure that the id is generated and revision is null for saving a new entity
            entity.setId(UUID.randomUUID().toString());
            entity.setRevision(null);
            add(entity);
            return true;
        }

        update(entity);
        return false;
    }

    @Override
    public ITrip get(UUID id) {
        try {
            return get(id.toString());
        } catch (DocumentNotFoundException e) {
            return null;
        }
    }

    @Override
    public List<ITrip> loadAll() {
        List<ITrip> trips = new LinkedList<ITrip>(getAll());
        return trips;
    }

    @Override
    public void delete(UUID id) {
        remove((Trip) get(id));
    }

    @Override
    public boolean close() {
        return true;
    }

    @Override
    public List<? extends ITrip> queryViews(final String viewName, final String key) {
        try {
            return super.queryView(viewName, key);
        } catch (DocumentNotFoundException e) {
            return new ArrayList<Trip>();
        }
    }

    @Override
    public void create(ModelDocument doc) {
        connector.create(doc);
    }

    @Override
    public void update(ModelDocument document) {
        connector.update(document);
    }


}