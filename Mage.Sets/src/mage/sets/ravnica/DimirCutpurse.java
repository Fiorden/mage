/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.sets.ravnica;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.DealsCombatDamageToAPlayerTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.game.Game;
import mage.players.Player;

/**
 *
 * @author jeffwadsworth
 *
 */
public class DimirCutpurse extends CardImpl {

    public DimirCutpurse(UUID ownerId) {
        super(ownerId, 201, "Dimir Cutpurse", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{1}{U}{B}");
        this.expansionSetCode = "RAV";
        this.subtype.add("Spirit");

        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        // Whenever Dimir Cutpurse deals combat damage to a player, that player discards a card and you draw a card.
        this.addAbility(new DealsCombatDamageToAPlayerTriggeredAbility(new DimirCutpurseEffect(), false, true));

    }

    public DimirCutpurse(final DimirCutpurse card) {
        super(card);
    }

    @Override
    public DimirCutpurse copy() {
        return new DimirCutpurse(this);
    }
}

class DimirCutpurseEffect extends OneShotEffect {

    DimirCutpurseEffect() {
        super(Outcome.Neutral);
        staticText = "that player discards a card and you draw a card";
    }

    DimirCutpurseEffect(final DimirCutpurseEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player you = game.getPlayer(source.getControllerId());
        Player damagedPlayer = game.getPlayer(targetPointer.getFirst(game, source));
        if (damagedPlayer != null) {
            damagedPlayer.discard(1, source, game);
        }
        if (you != null) {
            you.drawCards(1, game);
        }
        return true;
    }

    @Override
    public DimirCutpurseEffect copy() {
        return new DimirCutpurseEffect(this);
    }
}
