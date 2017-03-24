using System.Runtime.InteropServices;
using Robocode;
using Robocode.RobotInterfaces;

namespace Jsn
{
    class Target
    {
        private const int NotSeenTimeout = 20;

        public string Name { get; set; }
        public double Bearing { get; set; }
        public double Energy { get; set; }
        public double Heading { get; set; }
        public double Distance { get; set; }
        public Position Position { get; set; }
        public long TimeOfScan { get; set; }
        public double Velocity { get; set; }
        private Jsn _robot;

        public Target(ScannedRobotEvent e, Jsn robot)
        {
            Update(e, robot);
        }

        public void Update(ScannedRobotEvent e, Jsn robot)
        {
            _robot = robot;
            Energy = e.Energy;
            Distance = e.Distance;
            Bearing = e.Bearing;
            Heading = e.Heading;
            TimeOfScan = robot.Time;
            Velocity = e.Velocity;
            Position = new Position(_robot, Bearing, Distance);
        }

        public Position GetFuturePosition(long timeDelta)
        {
            return Position.Move(Heading, Velocity, timeDelta);
        }

        public bool IsOutdated(long time)
        {
            return TimeOfScan + NotSeenTimeout < time;
        }
        
    }

}
