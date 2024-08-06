use-sender

# Exercise 4 > Use Sender

In this exercise, we will use the sender to respond from `CoffeeHouse`.

- Change `CoffeeHouse` as follows:
    - Instead of logging "Coffee Brewing", respond to the `sender()`.
- In `CoffeeHouseApp` create an anonymous actor that does the following:
    - In the constructor send `CoffeeHouse` a "Brew Coffee" message.
    - Pass `self()` as a sender
    - Log the message received at `info`.
    - **HINT**: Creating anonymous actor:

```java
Props.create(AbstractLoggingActor.class, () -> new AbstractLoggingActor(){
    {
        coffeeHouse.tell("Brew Coffee", self());
    }
    @Override
    public Receive createReceive() {
        return receiveBuilder().matchAny(o -> log().info(o.toString())).build();
    }
})  
```

- Use the `run` command to boot the `CoffeeHouseApp` and verify:
    - `CoffeeHouse Open` is logged to `coffee-house.log`.
    - `Coffee Brewing` is logged to `coffee-house.log`.
- Use the `test` command to verify the solution works as expected.
- Use the `nextExercise` command to move to the next exercise.
