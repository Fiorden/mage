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
package mage.sets.visions;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.RequirementEffect;
import mage.abilities.effects.common.combat.MustBeBlockedByAllSourceEffect;
import mage.abilities.keyword.FlyingAbility;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.permanent.Permanent;

/**
 *
 * @author nigelzor
 */
public class TalruumPiper extends CardImpl {

    public TalruumPiper(UUID ownerId) {
        super(ownerId, 98, "Talruum Piper", Rarity.UNCOMMON, new CardType[]{CardType.CREATURE}, "{4}{R}");
        this.expansionSetCode = "VIS";
        this.subtype.add("Minotaur");
        this.power = new MageInt(3);
        this.toughness = new MageInt(3);

        // All creatures with flying able to block Talruum Piper do so.
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new TalruumPiperEffect()));
    }

    public TalruumPiper(final TalruumPiper card) {
        super(card);
    }

    @Override
    public TalruumPiper copy() {
        return new TalruumPiper(this);
    }
}

class TalruumPiperEffect extends RequirementEffect {

    public TalruumPiperEffect() {
        super(Duration.WhileOnBattlefield);
        staticText = "All creatures with flying able to block {this} do so";
    }

    public TalruumPiperEffect(TalruumPiperEffect effect) {
        super(effect);
    }

    @Override
    public boolean applies(Permanent permanent, Ability source, Game game) {
        Permanent sourceCreature = game.getPermanent(source.getSourceId());
        if (sourceCreature != null && sourceCreature.isAttacking()) {
            return permanent.getAbilities().contains(FlyingAbility.getInstance())
                    && permanent.canBlock(source.getSourceId(), game);
        }
        return false;
    }

    @Override
    public boolean mustAttack(Game game) {
        return false;
    }

    @Override
    public boolean mustBlock(Game game) {
        return true;
    }

    @Override
    public UUID mustBlockAttacker(Ability source, Game game) {
        return source.getSourceId();
    }

    @Override
    public TalruumPiperEffect copy() {
        return new TalruumPiperEffect(this);
    }
}
