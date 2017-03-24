namespace Juraj 

open Robocode


type Juraj() = 
    inherit Robot()
    
    let mutable isInitScan = false;

    let closestPoint x y points =
        let minDist = 
            points 
            |> List.map (fun (pX,pY) -> sqrt((pX-x)**2. + (pY-y)**2.))
            |> List.mapi (fun i v -> (i,v))
            |> List.minBy snd
        points.Item (fst minDist)

    let chooseBulletSize dist =
        if dist < 100. then 3.
        elif dist < 500. then 1.5
        elif dist < 750. then 1.
        else 0.5

    let absoluteBearing x y px py =
        int (atan2 (px - x) (py - y) * 180.0 / 3.14159);

    let normalRelativeAngle angle =
        let relativeAngle = angle % 360
        if relativeAngle <= -180 then
            float (180 + (relativeAngle % 180))
        elif relativeAngle > 180 then
            float (-180 + (relativeAngle % 180))
        else float relativeAngle
    
    let angleAndDistToPoint (head:float) (x:float) (y:float) (px:float) (py:float) =
        let mutable distance = sqrt ((px-x)**2. + (py-y)**2.)
        let mutable angle = normalRelativeAngle ( (absoluteBearing x y px py) - (int head))
        if (abs angle > 90.0) then 
            distance <- -1.0 * distance;
            if (angle > 0.0) then
                angle <- angle - 180.0
            else 
                angle <- angle + 180.0
        (angle,distance)

    let gunTurnAngle x y xDiff yDiff = 
        if yDiff > 150. then
            if x < 300. then 90.
            else 270.
        elif yDiff < -150. then
            if x < 300. then 270.
            else 90.
        elif xDiff > 150. then
            if y < 300. then 90.
            else 270.
        else
            if y < 300. then 270.
            else 90.
        

        
    override this.Run() = 
        this.BodyColor <- System.Drawing.Color.Purple
        this.TurnRadarRight 360.

        let points = [(100.,100.); (900.,100.); (100.,900.); (900.,900.)]
        let rand = System.Random()


        while true do
            let corner = rand.Next(3)
            let (px,py) = points.[corner]
            let angle,dist = angleAndDistToPoint this.Heading this.X this.Y px py
            this.TurnRight angle
            let gunAngle = gunTurnAngle this.X this.Y (px-this.X) (py-this.Y)
            let correctAngle =
                match dist, gunAngle with
                | dist, 90. when dist < 0. -> 270.
                | dist, 270. when dist < 0. -> 90.
                | _,_ -> gunAngle
            this.TurnGunRight correctAngle 
            this.Ahead dist
            this.TurnGunLeft correctAngle 
        ()

    override this.OnScannedRobot(args) =
        match isInitScan with
        | true -> 
            this.Fire (chooseBulletSize args.Distance)
            this.TurnRight (args.Bearing + 180.)
            this.Ahead 400.
            isInitScan <- false
        | false ->
            this.Fire (chooseBulletSize args.Distance)
        ()

    override this.OnHitByBullet(args) =
        this.TurnLeft (this.Heading - 90.)
        this.TurnGunRight 90.
        ()
