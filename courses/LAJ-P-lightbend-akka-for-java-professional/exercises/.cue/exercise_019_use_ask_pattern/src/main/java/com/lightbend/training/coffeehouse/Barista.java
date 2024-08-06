/*
 * Copyright © 2014, 2015, 2016 Lightbend, Inc. All rights reserved.
 */

package com.lightbend.training.coffeehouse;

import akka.actor.AbstractActorWithStash;
import akka.actor.ActorRef;
import akka.actor.Props;
import scala.concurrent.duration.FiniteDuration;

import java.util.Random;

import static com.google.common.base.Preconditions.checkNotNull;

public class Barista extends AbstractActorWithStash{

    private final FiniteDuration prepareCoffeeDuration;

    private final int accuracy;

    public Barista(FiniteDuration prepareCoffeeDuration, int accuracy){
        this.prepareCoffeeDuration = prepareCoffeeDuration;
        this.accuracy = accuracy;
    }

    public static Props props(FiniteDuration prepareCoffeeDuration, int accuracy){
        return Props.create(Barista.class, () -> new Barista(prepareCoffeeDuration, accuracy));
    }

    @Override
    public Receive createReceive() {
        return ready();
    }

    private Receive ready(){
        return receiveBuilder().
            match(PrepareCoffee.class, prepareCoffee -> {
                scheduleCoffeePrepared(prepareCoffee.coffee, prepareCoffee.guest);
                getContext().become(busy(sender()));
            }).
            matchAny(this::unhandled).build();
    }

    private Receive busy(ActorRef waiter){
        return receiveBuilder().
            match(CoffeePrepared.class, coffeePrepared -> {
                waiter.tell(coffeePrepared, self());
                unstashAll();
                getContext().become(ready());
            }).
            matchAny(msg -> stash()).build();
    }

    private Coffee pickCoffee(Coffee coffee){
        return new Random().nextInt(100) < accuracy ? coffee : Coffee.orderOther(coffee);
    }

    private void scheduleCoffeePrepared(Coffee coffee, ActorRef guest){
        context().system().scheduler().scheduleOnce(prepareCoffeeDuration, self(),
            new CoffeePrepared(pickCoffee(coffee), guest), context().dispatcher(), self());
    }

    public static final class PrepareCoffee{

        public final Coffee coffee;

        public final ActorRef guest;

        public PrepareCoffee(final Coffee coffee, final ActorRef guest){
            checkNotNull(coffee, "Coffee cannot be null");
            checkNotNull(guest, "Guest cannot be null");
            this.coffee = coffee;
            this.guest = guest;
        }

        @Override
        public String toString(){
            return "PrepareCoffee{"
                + "coffee=" + coffee + ", "
                + "guest=" + guest + "}";
        }

        @Override
        public boolean equals(Object o){
            if (o == this) return true;
            if (o instanceof PrepareCoffee) {
                PrepareCoffee that = (PrepareCoffee) o;
                return (this.coffee.equals(that.coffee))
                    && (this.guest.equals(that.guest));
            }
            return false;
        }

        @Override
        public int hashCode(){
            int h = 1;
            h *= 1000003;
            h ^= coffee.hashCode();
            h *= 1000003;
            h ^= guest.hashCode();
            return h;
        }
    }

    public static final class CoffeePrepared{

        public final Coffee coffee;

        public final ActorRef guest;

        public CoffeePrepared(final Coffee coffee, final ActorRef guest){
            checkNotNull(coffee, "Coffee cannot be null");
            checkNotNull(guest, "Guest cannot be null");
            this.coffee = coffee;
            this.guest = guest;
        }

        @Override
        public String toString(){
            return "CoffeePrepared{"
                + "coffee=" + coffee + ", "
                + "guest=" + guest + "}";
        }

        @Override
        public boolean equals(Object o){
            if (o == this) return true;
            if (o instanceof CoffeePrepared) {
                CoffeePrepared that = (CoffeePrepared) o;
                return (this.coffee.equals(that.coffee))
                    && (this.guest.equals(that.guest));
            }
            return false;
        }

        @Override
        public int hashCode(){
            int h = 1;
            h *= 1000003;
            h ^= coffee.hashCode();
            h *= 1000003;
            h ^= guest.hashCode();
            return h;
        }
    }
}
