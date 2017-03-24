namespace CN.Dzoukr.Robocode

open Robocode

type DzoukrBot() =
    inherit Robot()

    override this.Run() = 
        this.BodyColor <- System.Drawing.Color.Red
        
        while true do
            this.TurnRight (this.Heading + 10.)
            this.Ahead 100.
            this.BodyColor <- System.Drawing.Color.LightSeaGreen
        ()


    override this.OnScannedRobot(args) =
        this.TurnGunRight args.Bearing
        this.Fire 10.
        ()
    
    //override this.OnHitWall(args) =
    //    args.
