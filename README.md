# mafia-roller
Mafia roller bot in kotlin

Bot for [@truemafiabot](https://t.me/truemafiabot). Requires authorization only at first launch. To clear saved data, delete '/properties/' dir in script's launch directory

# Usage
Clone project and run com.y9san9.mafiaboost.Main.kt

## Easy way Windows
- Download [Java](https://www.java.com/ru/download/) (Do not forget to add PATH variable)
- Download [Git](https://git-scm.com/downloads)

Open cmd and exec: <br>
- `git clone https://github.com/y9san9/mafia-roller/`
- `cd mafia-roller`
- `java -jar app.jar`

## For Linux
You can just check steps before, that will be similar

## For android
- Install [Termux](https://play.google.com/store/apps/details?id=com.termux)

Open and run:
- `pkg install dx`
- `pkg install git`
- `git clone https://github.com/y9san9/mafia-roller/`
- `cd mafia-roller`
- `dx --dex --output=app-dexed.jar app.jar`
- `dalvikvm -cp app-dexed.jar com.y9san9.mafiaboost.MainKt`
