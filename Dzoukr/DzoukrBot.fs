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
    
    override this.Run() = 
        this.BodyColor <- config.Color
        
        while true do
            config 
            |> calculateNextActions this
            |> applyActions this
        ()

    override this.OnScannedRobot(args) =
        args 
        |> calculateScannedBotActions this 
        |> applyActions this
        ()
    
    override this.OnHitRobot(args) =
        args 
        |> calculateHitBotActions this 
        |> applyActions this
        ()
    
    override this.OnHitByBullet(args) =
        args 
        |> calculateHitByBulletActions this 
        |> applyActions this
        ()
   