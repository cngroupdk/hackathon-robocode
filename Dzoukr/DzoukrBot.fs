namespace CN.Dzoukr.Robocode

open Robocode
open System
open FSharp.Core
open Core

type DzoukrBot() =
    inherit Robot()

    let config = {
        Color = System.Drawing.Color.HotPink
        WallLimit = 50.
    }

    //let escapeMove = walllimit * 0.5
    //let panicEscapeMove = 200.
    //let attackMove = 100.
    
    //let getHeading (me:DzoukrBot) bearing = me.Heading - me.GunHeading + bearing
    //let getFirePower (enemyDistance:float) = 400. / enemyDistance

    //member this.ShootToEnemy enDistance enBearing =
    //    let firePower = enDistance |> getFirePower
    //    if this.Energy > firePower then
    //        enBearing |> getHeading this |> this.TurnRight
    //        firePower |> this.Fire
    
    override this.Run() = 
        this.BodyColor <- config.Color
        
        while true do
            config 
            |> calculateNextAction this
            |> applyAction this
        ()

    override this.OnScannedRobot(args) =
        args 
        |> calculateScannedBotActions this 
        |> applyActions this
        //this.ShootToEnemy args.Distance args.Bearing
        //attackMove |> this.Ahead
        ()
    
    override this.OnHitRobot(args) =
        //this.ShootToEnemy 0.1 args.Bearing
        ()
    
    override this.OnHitByBullet(args) =
        //this.ShootToEnemy 1. args.Bearing
        //attackMove |> this.Ahead
        ()
   