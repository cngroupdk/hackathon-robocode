module CN.Dzoukr.Robocode.Core

open Robocode
open System

type Setup = {
    Color : System.Drawing.Color
    WallLimit : float
}

type PositionOnField = {
    X : float
    Y : float
    FieldWidth : float
    FieldHeight : float
}
with 
    static member create(robot:Robot) = {
        X = robot.X
        Y = robot.Y
        FieldWidth = robot.BattleFieldWidth
        FieldHeight = robot.BattleFieldHeight
    }

type WallHitAlert =
    | CloseToLeft
    | CloseToRight
    | CloseToTop
    | CloseToBottom

let (|WallHitAlert|_|) (config, position, heading) =
    if position.X <= config.WallLimit then
        Some CloseToLeft
    else if position.X >= position.FieldWidth - config.WallLimit then
        Some CloseToRight
    else if position.Y <= config.WallLimit then
        Some CloseToBottom
    else if position.Y >= position.FieldHeight - config.WallLimit then
        Some CloseToTop
    else None

type Action =
    | Move of degrees:float * distance:float
    | Fire of degrees:float * power:float
    | Scan of degrees:float

let normalize value =
    if value < 0. then
        360. - value
    else if value > 360. then
        value - 360.
    else value
    

let randomWithCenter center =
    let center = int center
    let min,max = (center - 22),(center + 22)
    System.Random().Next(min, max) |> float |> normalize

let calculateNextActions (robot:Robot) config  =
    let position = PositionOnField.create(robot)
    let moveAway = config.WallLimit * 1.5
    match config, position, robot.Heading with
    | WallHitAlert(CloseToLeft) -> [Move(randomWithCenter 90., moveAway)]
    | WallHitAlert(CloseToRight) -> [Move(randomWithCenter 270., moveAway)]
    | WallHitAlert(CloseToTop) -> [Move(randomWithCenter 180., moveAway)]
    | WallHitAlert(CloseToBottom) -> [Move(randomWithCenter 0., moveAway)]
    | _ -> 
        [
            Move(robot.Heading, 100.); 
            Scan(robot.RadarHeading + 25. |> normalize)
        ]

let getPowerByDistance dist = 400. / dist

let calculateScannedBotActions (robot:Robot) (args:ScannedRobotEvent)  =
    let heading = robot.Heading + args.Bearing
    let firePower = args.Distance |> getPowerByDistance
    [
        Fire(heading, firePower)
        Scan(heading)
        Move(heading, 50.)
    ]

let calculateHitBotActions (robot:Robot) (args:HitRobotEvent)  =
    let heading = robot.Heading + args.Bearing
    let firePower = 3.
    [
        Scan(heading)
        Fire(heading, firePower)
    ]

let effectiveTurn (heading:float) turnLeftF turnRighF newHeading =
    match heading, newHeading with
    | h,nh when h = nh -> ()
    | h,nh ->
        let diff = Math.Abs(h-nh)
        if h > nh then
            turnLeftF diff
        else turnRighF diff

let applyAction (robot:Robot) = function
    | Move(degrees, distance) -> 
        degrees |> effectiveTurn robot.Heading robot.TurnLeft robot.TurnRight
        robot.Ahead(distance)
    | Fire(degrees, power) ->
        if robot.Energy > power then
            degrees |> effectiveTurn robot.Heading robot.TurnLeft robot.TurnRight
            robot.Fire(power)
        
    | Scan(degrees) ->
        degrees |> effectiveTurn robot.RadarHeading robot.TurnRadarLeft robot.TurnRadarRight

let applyActions (robot:Robot) = List.iter (applyAction robot)