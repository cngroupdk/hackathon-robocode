namespace CN.Dzoukr.Robocode

open Robocode
open System
open FSharp.Core

type DzoukrBot() =
    inherit Robot()

    let walllimit = 100.
    let escapeMove = walllimit * 0.5
    let panicEscapeMove = 200.
    let attackMove = 100.

    let isWallAlert (me:DzoukrBot)  = 
        me.X <= walllimit 
        || me.X >= me.BattleFieldWidth - walllimit
        || me.Y <= walllimit
        || me.Y >= me.BattleFieldHeight - walllimit
    
    let getHeading (me:DzoukrBot) bearing = me.Heading - me.GunHeading + bearing
    let getFirePower (enemyDistance:float) = 400. / enemyDistance

    member this.ShootToEnemy enDistance enBearing =
        let firePower = enDistance |> getFirePower
        if this.Energy > firePower then
            enBearing |> getHeading this |> this.TurnRight
            firePower |> this.Fire
    
    override this.Run() = 
        this.BodyColor <- System.Drawing.Color.HotPink
        
        while true do
            if this |> isWallAlert then
                this.TurnRight (45.)
                this.Ahead escapeMove
            
            this.TurnRadarRight 25.

            if this.Time%10L = 0L then this.Ahead 100.
        ()

    override this.OnScannedRobot(args) =
        this.ShootToEnemy args.Distance args.Bearing
        attackMove |> this.Ahead
        ()
    
    override this.OnHitRobot(args) =
        this.ShootToEnemy 0.1 args.Bearing
    
    override this.OnHitByBullet(args) =
        this.ShootToEnemy 1. args.Bearing
        attackMove |> this.Ahead
        ()
   