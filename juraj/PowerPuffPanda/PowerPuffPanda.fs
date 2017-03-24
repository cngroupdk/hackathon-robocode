namespace Juraj 

open Robocode

type PowerPuffPanda() = 
    inherit Robot()
    
    let mutable isInitScan = false;

    //let closestPoint x y points =
    //    (0.,0.
        //let minDist = 
        //    points 
        //    |> List.map (fun (pX,pY) -> sqrt((pX-x)**2. + (pY-y)**2.))
        //    |> List.mapi (fun i v -> (i,v))
        //    |> List.minBy snd
        //points.Item (fst minDist)

    let chooseBulletSize dist =
        if dist < 100. then 3.
        elif dist < 500. then 1.5
        elif dist < 750. then 1.
        else 0.5

    let angleToPoint x y px py =
        let angle = (atan2 (px - x) (py - y) * 180.0 / 3.14159);
        if (angle > 0.0) then
            angle - 180.
        else angle + 180.

    let distToPoint x y px py = 
        sqrt((px-x)**2. + (py-y)**2.)  


    override this.Run() = 
        this.BodyColor <- System.Drawing.Color.Purple
        this.TurnRadarRight 360.

        //let points = [(50.,50.); (950.,50.); (50.,950.); (950.,950.)]
        //let rand = System.Random()

        //this.TurnLeft (this.Heading - 90.)
        //this.TurnGunRight 90.

        while true do
            //let corner = rand.Next(3)
            //let corner = 3
            //let (px,py) = points.[corner]
            //let angle = angleToPoint this.X this.Y px py
            //let dist = distToPoint this.X this.Y px py
            //this.TurnRight angle
            //this.Ahead dist
            this.TurnRight 90. 
            this.TurnRadarRight 360.
            this.Ahead 350. 
        ()

    override this.OnScannedRobot(args) =
        match isInitScan with
        | true -> 
            this.Fire (chooseBulletSize args.Distance)
            this.TurnRight (args.Bearing + 180.)
            this.Ahead 150.
            isInitScan <- false
        | false ->
            this.Fire (chooseBulletSize args.Distance)
        ()

    override this.OnHitByBullet(args) =
        this.TurnRight args.Bearing
        ()
