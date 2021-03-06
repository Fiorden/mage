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
package mage.sets.lorwyn;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.effects.ContinuousEffect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.ClashEffect;
import mage.abilities.effects.common.continuous.BoostTargetEffect;
import mage.abilities.effects.common.continuous.GainAbilityTargetEffect;
import mage.abilities.keyword.TrampleAbility;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.common.TargetCreaturePermanent;
import mage.target.targetpointer.FixedTarget;

/**
 *
 * @author LevelX2
 */
public class FistfulOfForce extends CardImpl {

    public FistfulOfForce(UUID ownerId) {
        super(ownerId, 212, "Fistful of Force", Rarity.COMMON, new CardType[]{CardType.INSTANT}, "{1}{G}");
        this.expansionSetCode = "LRW";


        // Target creature gets +2/+2 until end of turn. Clash with an opponent. If you win, that creature gets an additional +2/+2 and gains trample until end of turn.
        this.getSpellAbility().addEffect(new FistfulOfForceEffect());
        this.getSpellAbility().addTarget(new TargetCreaturePermanent());
    }

    public FistfulOfForce(final FistfulOfForce card) {
        super(card);
    }

    @Override
    public FistfulOfForce copy() {
        return new FistfulOfForce(this);
    }
}

class FistfulOfForceEffect extends OneShotEffect {

    public FistfulOfForceEffect() {
        super(Outcome.BoostCreature);
        this.staticText = "Target creature gets +2/+2 until end of turn. Clash with an opponent. If you win, that creature gets an additional +2/+2 and gains trample until end of turn";
    }

    public FistfulOfForceEffect(final FistfulOfForceEffect effect) {
        super(effect);
    }

    @Override
    public FistfulOfForceEffect copy() {
        return new FistfulOfForceEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        Permanent creature = game.getPermanent(getTargetPointer().getFirst(game, source));
        if (controller != null && creature != null) {
            ContinuousEffect effect = new BoostTargetEffect(2,2,Duration.EndOfTurn);
            effect.setTargetPointer(new FixedTarget(creature.getId()));
            game.addEffect(effect, source);
            if (ClashEffect.getInstance().apply(game, source)) {
                game.addEffect(effect.copy(), source);
                effect = new GainAbilityTargetEffect(TrampleAbility.getInstance(), Duration.EndOfTurn);
                effect.setTargetPointer(new FixedTarget(creature.getId()));
                game.addEffect(effect.copy(), source);
            }
            return true;
        }
        return false;
    }
}
