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
package mage.sets.theros;

import java.util.UUID;
import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.effects.AsThoughEffectImpl;
import mage.abilities.effects.AsThoughManaEffect;
import mage.abilities.effects.ContinuousEffect;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.constants.AsThoughEffectType;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.ManaType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.filter.common.FilterNonlandCard;
import mage.game.Game;
import mage.players.ManaPoolItem;
import mage.players.Player;
import mage.target.TargetCard;
import mage.target.common.TargetOpponent;
import mage.target.targetpointer.FixedTarget;
import mage.util.CardUtil;

/**
 *
 * @author LevelX2
 */
public class PsychicIntrusion extends CardImpl {

    public PsychicIntrusion(UUID ownerId) {
        super(ownerId, 200, "Psychic Intrusion", Rarity.RARE, new CardType[]{CardType.SORCERY}, "{3}{U}{B}");
        this.expansionSetCode = "THS";

        // Target opponent reveals his or her hand. You choose a nonland card from that player's graveyard or hand and exile it.
        // You may cast that card for as long as it remains exiled, and you may spend mana as though it were mana of any color
        // to cast that spell.
        this.getSpellAbility().addTarget(new TargetOpponent());
        this.getSpellAbility().addEffect(new PsychicIntrusionExileEffect());

    }

    public PsychicIntrusion(final PsychicIntrusion card) {
        super(card);
    }

    @Override
    public PsychicIntrusion copy() {
        return new PsychicIntrusion(this);
    }
}

class PsychicIntrusionExileEffect extends OneShotEffect {

    private static final FilterNonlandCard filter = new FilterNonlandCard();

    public PsychicIntrusionExileEffect() {
        super(Outcome.Benefit);
        this.staticText = "Target opponent reveals his or her hand. You choose a nonland card from that player's graveyard or hand and exile it. You may cast that card for as long as it remains exiled, and you may spend mana as though it were mana of any color to cast that spell";
    }

    public PsychicIntrusionExileEffect(final PsychicIntrusionExileEffect effect) {
        super(effect);
    }

    @Override
    public PsychicIntrusionExileEffect copy() {
        return new PsychicIntrusionExileEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player opponent = game.getPlayer(targetPointer.getFirst(game, source));
        MageObject sourceObject = game.getObject(source.getSourceId());
        if (opponent != null && sourceObject != null) {
            opponent.revealCards(sourceObject.getName(), opponent.getHand(), game);
            Player controller = game.getPlayer(source.getControllerId());
            if (controller != null) {
                int cardsGraveyard = opponent.getGraveyard().count(filter, game);
                int cardsHand = opponent.getHand().count(filter, game);
                boolean fromHand = false;
                if (cardsGraveyard > 0 && cardsHand > 0) {
                    if (controller.chooseUse(Outcome.Detriment, "Exile card from opponents Hand?", source, game)) {
                        fromHand = true;
                    }
                } else {
                    if (cardsHand > 0) {
                        fromHand = true;
                    }
                }

                Card card = null;
                if (cardsHand > 0 && fromHand) {
                    TargetCard target = new TargetCard(Zone.HAND, filter);
                    if (controller.choose(Outcome.Benefit, opponent.getHand(), target, game)) {
                        card = opponent.getHand().get(target.getFirstTarget(), game);

                    }
                }
                if (cardsGraveyard > 0 && !fromHand) {
                    TargetCard target = new TargetCard(Zone.GRAVEYARD, filter);
                    if (controller.choose(Outcome.Benefit, opponent.getGraveyard(), target, game)) {
                        card = opponent.getGraveyard().get(target.getFirstTarget(), game);

                    }
                }
                if (card != null) {
                    // move card to exile
                    UUID exileId = CardUtil.getCardExileZoneId(game, source);
                    controller.moveCardToExileWithInfo(card, exileId, sourceObject.getIdName(), source.getSourceId(), game, fromHand ? Zone.HAND : Zone.GRAVEYARD, true);
                    // allow to cast the card
                    ContinuousEffect effect = new PsychicIntrusionCastFromExileEffect();
                    effect.setTargetPointer(new FixedTarget(card.getId()));
                    game.addEffect(effect, source);
                    // and you may spend mana as though it were mana of any color to cast it
                    effect = new PsychicIntrusionSpendAnyManaEffect();
                    effect.setTargetPointer(new FixedTarget(card.getId()));
                    game.addEffect(effect, source);
                }
                return true;
            }
        }
        return false;
    }
}

class PsychicIntrusionCastFromExileEffect extends AsThoughEffectImpl {

    public PsychicIntrusionCastFromExileEffect() {
        super(AsThoughEffectType.PLAY_FROM_NOT_OWN_HAND_ZONE, Duration.Custom, Outcome.Benefit);
        staticText = "You may cast that card for as long as it remains exiled, and you may spend mana as though it were mana of any color to cast that spell";
    }

    public PsychicIntrusionCastFromExileEffect(final PsychicIntrusionCastFromExileEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        return true;
    }

    @Override
    public PsychicIntrusionCastFromExileEffect copy() {
        return new PsychicIntrusionCastFromExileEffect(this);
    }

    @Override
    public boolean applies(UUID objectId, Ability source, UUID affectedControllerId, Game game) {
        if (objectId.equals(getTargetPointer().getFirst(game, source))) {
            if (affectedControllerId.equals(source.getControllerId())) {
                return true;
            }
        } else {
            if (((FixedTarget) getTargetPointer()).getTarget().equals(objectId)) {
                // object has moved zone so effect can be discarted
                this.discard();
            }
        }
        return false;
    }
}

class PsychicIntrusionSpendAnyManaEffect extends AsThoughEffectImpl implements AsThoughManaEffect {

    public PsychicIntrusionSpendAnyManaEffect() {
        super(AsThoughEffectType.SPEND_OTHER_MANA, Duration.Custom, Outcome.Benefit);
        staticText = "you may spend mana as though it were mana of any color to cast it";
    }

    public PsychicIntrusionSpendAnyManaEffect(final PsychicIntrusionSpendAnyManaEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        return true;
    }

    @Override
    public PsychicIntrusionSpendAnyManaEffect copy() {
        return new PsychicIntrusionSpendAnyManaEffect(this);
    }

    @Override
    public boolean applies(UUID objectId, Ability source, UUID affectedControllerId, Game game) {
        if (objectId.equals(((FixedTarget) getTargetPointer()).getTarget())
                && game.getState().getZoneChangeCounter(objectId) <= ((FixedTarget) getTargetPointer()).getZoneChangeCounter() + 1) {

            if (affectedControllerId.equals(source.getControllerId())) {
                // if the card moved from exile to spell the zone change counter is increased by 1
                if (game.getState().getZoneChangeCounter(objectId) == ((FixedTarget) getTargetPointer()).getZoneChangeCounter() + 1) {
                    return true;
                }
            }

        } else {
            if (((FixedTarget) getTargetPointer()).getTarget().equals(objectId)) {
                // object has moved zone so effect can be discarted
                this.discard();
            }
        }
        return false;
    }

    @Override
    public ManaType getAsThoughManaType(ManaType manaType, ManaPoolItem mana, UUID affectedControllerId, Ability source, Game game) {
        return mana.getFirstAvailable();
    }
}
