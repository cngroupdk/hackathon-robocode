using System.Runtime.InteropServices;
using Robocode;
using Robocode.RobotInterfaces;

namespace Jsn
{
    class Target
    {
        private const int NotSeenTimeout = 20;

        public string Name { get; set; }
        public double BearingRadians { get; set; }
        public double Bearing { get; set; }
        public double Energy { get; set; }
        public double HeadingRadians { get; set; }
        public double Heading { get; set; }
        public double Distance { get; set; }
        public Position Position { get; set; }
        public long TimeOfScan { get; set; }
        public double Velocity { get; set; }
        private Robot _robot;

        public Target(ScannedRobotEvent e, Robot robot)
        {
            Update(e, robot);
        }

        public void Update(ScannedRobotEvent e, Robot robot)
        {
            _robot = robot;
            Name = e.Name;
            BearingRadians = e.BearingRadians;
            Energy = e.Energy;
            HeadingRadians = e.HeadingRadians;
            Distance = e.Distance;
            Bearing = e.Bearing;
            Heading = e.Heading;
            TimeOfScan = robot.Time;
            Velocity = e.Velocity;
            Position = new Position(_robot, BearingRadians, Distance);
        }

        public Position GetFuturePosition(long timeDelta)
        {
            return Position.Move(HeadingRadians, Velocity, timeDelta);
        }

        public bool IsOutdated(long time)
        {
            return TimeOfScan + NotSeenTimeout < time;
        }
        
    }

}
