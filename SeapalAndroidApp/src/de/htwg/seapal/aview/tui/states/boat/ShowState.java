package de.htwg.seapal.aview.tui.states.boat;

import java.util.UUID;

import android.content.Intent;
import android.widget.Toast;
import de.htwg.seapal.aview.tui.StateContext;
import de.htwg.seapal.aview.tui.TuiState;
import de.htwg.seapal.aview.tui.activity.BoatActivity;
import de.htwg.seapal.aview.tui.activity.TripActivity;

public class ShowState implements TuiState {

	private final UUID boat;

	public ShowState(UUID boat) {
		this.boat = boat;
	}

	@Override
	public String buildString(StateContext context) {
		StringBuilder sb = new StringBuilder();
		sb.append("q - quit\n");
		sb.append("d - delete boat\n");
		sb.append("e - edit\n");
		sb.append("t - show trips\n");
		sb.append("--------------------------------------------------\n");
		sb.append(((BoatActivity) context).getController().getString(boat));
		return sb.toString();
	}

	@Override
	public void process(StateContext context, String input) {
		BoatActivity activity = (BoatActivity) context;
		switch (input.charAt(0)) {
		case 'q':
			context.setState(new StartState());
			break;
		case 'd':
			context.setState(new StartState());
			activity.getController().deleteBoat(boat);
			break;
		case 'e':
			context.setState(new EditSelectState(boat));
			break;
		case 't':
			Intent intent = new Intent(activity, TripActivity.class);
			intent.putExtra("boat", boat.toString());
			activity.startActivity(intent);
			break;
		default:
			Toast.makeText(activity, "Unkown Option", Toast.LENGTH_SHORT)
					.show();
        }

    }

}
