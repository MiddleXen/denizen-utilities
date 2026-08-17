package com.isnsest.denizenutilities.bridges.discordsrv;

import com.isnsest.denizenutilities.bridges.discordsrv.events.PlayerLinksDiscordAccountScriptEvent;
import com.isnsest.denizenutilities.bridges.discordsrv.events.PlayerUnlinksDiscordAccountScriptEvent;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.AccountLinkedEvent;
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent;

public class DiscordSRVListener {

    @Subscribe
    public void accountsLinked(AccountLinkedEvent event) {
        PlayerLinksDiscordAccountScriptEvent.instance.fire(event);
    }

    @Subscribe
    public void accountUnlinked(AccountUnlinkedEvent event) {
        PlayerUnlinksDiscordAccountScriptEvent.instance.fire(event);
    }
}
