package de.lycantrophia.addon.serverstatebutton.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.Connect;
import de.lycantrophia.addon.serverstatebutton.ServerStateButton;

// Connector binds client-side widget class to server-side component class
// Connector lives in the client and the @Connect annotation specifies the
// corresponding server-side component
@SuppressWarnings({"unused", "Convert2Lambda", "WeakerAccess"})
@Connect(ServerStateButton.class)
public class ServerStateButtonConnector extends AbstractComponentConnector {

	// ServerRpc is used to send events to server. Communication implementation
	// is automatically created here
	private ServerStateButtonServerRpc rpc = RpcProxy.create(ServerStateButtonServerRpc.class, this);

	public ServerStateButtonConnector() {
		
		// To receive RPC events from server, we register ClientRpc implementation 
		registerRpc(ServerStateButtonClientRpc.class, new ServerStateButtonClientRpc() {
			@Override public void setMaxRam(final int maxRam) {
				getWidget().setMaxRam(maxRam);
			}

			@Override public void setVertical(final boolean isVertivalLayout) {
				getWidget().setVertical(isVertivalLayout);
			}

			@Override public void setServerName(final String serverName)
			{
				getWidget().setServerName(serverName);
			}

			@Override public void updateServerInfo(final String users, final double cpuLoad, final int memoryUsage) {
				getWidget().setStatistics(users, cpuLoad, memoryUsage);
			}
		});

		// We choose listed for mouse clicks for the widget
		getWidget().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final MouseEventDetails mouseDetails = MouseEventDetailsBuilder
						.buildMouseEventDetails(event.getNativeEvent(),
								ServerStateButtonConnector.this.getWidget().getElement());

				// When the widget is clicked, the event is sent to server with ServerRpc
				rpc.clicked(mouseDetails);
			}
		});

	}

	// We must implement getWidget() to cast to correct type 
	// (this will automatically create the correct widget type)
	@Override
	public ServerStateButtonWidget getWidget() {
		return (ServerStateButtonWidget) super.getWidget();
	}

	// We must implement getState() to cast to correct type
	@Override
	public ServerStateButtonState getState() {
		return (ServerStateButtonState) super.getState();
	}

	// Whenever the state changes in the server-side, this method is called
	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);
		getWidget().setStatistics(getState().getUserCount(), getState().getCpuLoad(), getState().getRamUsage());
	}

}
