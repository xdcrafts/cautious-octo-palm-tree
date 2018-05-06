package com.github.xdcrafts.octopalm.api.account.events;

import com.github.xdcrafts.octopalm.api.account.Account;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.integration.annotation.Publisher;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@Publisher(channel = "auditChannel")
@RepositoryEventHandler
public class AccountEventHandler {

    @HandleAfterCreate
    public Message<Account> afterCreate(Account account) {
        return MessageBuilder
            .withPayload(account)
            .setHeader("action", "create")
            .setHeader("entityType", "action")
            .build();
    }

    @HandleAfterSave
    public Message<Account> afterSave(Account account) {
        return MessageBuilder
            .withPayload(account)
            .setHeader("action", "save")
            .setHeader("entityType", "action")
            .build();
    }
}
