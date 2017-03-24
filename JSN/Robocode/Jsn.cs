using System;
using System.Collections.Generic;
using Robocode;
using System.Drawing;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Security.Cryptography.X509Certificates;

namespace Jsn
{
    public class Jsn : Robot
    {
        public double WallMargin = 200;
        private Dictionary<string, Target> _targets = new Dictionary<string, Target>();
        private Position _position = new Position();
        private double _desiredHeading = 0;
        private int _strafeDirection = 1;
        private Target _lockedTarget;
        private double _desiredGunBearingDiff;
        private int _scanRange = 180;

        public override void Run()
        {
            IsAdjustGunForRobotTurn = false;
            IsAdjustRadarForGunTurn = false;
            IsAdjustRadarForRobotTurn = false;
            SetAllColors(Color.Orange);

            while (true)
            {
                // scan surroundings
                _position.Update(X, Y);
                TurnRadarRight(_scanRange * _strafeDirection);
                _lockedTarget = GetClosestTarget();
                if (_lockedTarget != null)
                {
                    // fire
                    var firePower = Math.Min(500 / _lockedTarget.Distance, 3);
                    if (_lockedTarget.Velocity < 0.5 || _lockedTarget.Distance < 50) firePower = 3;
                    var bulletSpeed = 20 - firePower * 3;
                    var time = (long) (_lockedTarget.Distance / bulletSpeed);
                    var futurePosition = _lockedTarget.GetFuturePosition(time);
                    _desiredGunBearingDiff = NormalizeBearing(_position.GetAbsoluteBearing(futurePosition) - GunHeading);

                    TurnGunRight(_desiredGunBearingDiff);
                    if (GunHeat < 0.1)
                    {
                        Fire(firePower);
                    }
                }

                Movement();
            }
        }

        private void Movement()
        {
            // base of number of oponents choose movement strategy
            if (Others > 3)
            {
                // edges
                if (AmICloseToWall())
                {
                    var quadrant = _position.GetQuadrant(BattleFieldHeight, BattleFieldWidth);
                    switch (quadrant)
                    {
                        case Quadrant.Ne:
                            _desiredHeading = 270;
                            break;
                        case Quadrant.Nw:
                            _desiredHeading = 180;
                            break;
                        case Quadrant.Se:
                            _desiredHeading = 0;
                            break;
                        case Quadrant.Sw:
                            _desiredHeading = 90;
                            break;
                    }
                }
                _desiredHeading += GetRandomNumber(-10, 10);

                TurnRight(NormalizeBearing(_desiredHeading - Heading));
                Ahead(185);
            }
            else
            {
                // strafe closing in
                WallMargin = 130;
                _scanRange = 45;

                if (_lockedTarget != null)
                {
                    var turnDeg = _lockedTarget.Bearing + 90 - (15 * _strafeDirection);
                    TurnRight(NormalizeBearing(turnDeg));
                    Ahead(GetRandomNumber(100, 220) * _strafeDirection);
                    _strafeDirection *= -1;
                }

                if (AmICloseToWall())
                {
                    TurnRight(_position.GetAbsoluteBearing(new Position(BattleFieldWidth / 2, BattleFieldHeight / 2)) - Heading);
                    Ahead(250);
                }
            }
        }


        public static double RadToDeg(double angle)
        {
            return angle * (180.0 / Math.PI);
        }

        public static double DegToRad(double angle)
        {
            return Math.PI * angle / 180.0;
        }

        public double GetRandomNumber(double minimum, double maximum)
        {
            Random random = new Random();
            return random.NextDouble() * (maximum - minimum) + minimum;
        }

        public double NormalizeBearing(double bearing)
        {
            var normalizedBearing = bearing;
            while (normalizedBearing > 180)
            {
                normalizedBearing -= 360;
            }
            while (normalizedBearing < -180)
            {
                normalizedBearing += 360;
            }
            return normalizedBearing;
        }

        private bool AmICloseToWall()
        {
            return (_position.X < WallMargin ||
                _position.X > (BattleFieldWidth - WallMargin) ||
                _position.Y < WallMargin ||
                _position.Y > (BattleFieldHeight - WallMargin));
        }


        public override void OnScannedRobot(ScannedRobotEvent e)
        {
            if (_targets.ContainsKey(e.Name))
            {
                _targets[e.Name].Update(e, this);
            }
            else
            {
                _targets.Add(e.Name, new Target(e, this));
            }

            // check if shot
        }

        public override void OnRobotDeath(RobotDeathEvent e)
        {
            // remove from targets
            if (_targets.ContainsKey(e.Name)) _targets.Remove(e.Name);
        }

        private Target GetClosestTarget()
        {
            if (_targets.Count > 0)
            {
                var minDist = _targets.Values.Min(t => t.Distance);
                return _targets.Values.First(a => a.Distance.Equals(minDist));
            }
            return null;
        }

        public override void OnHitRobot(HitRobotEvent e)
        {
            _targets.TryGetValue(e.Name, out _lockedTarget);
            Movement();
        }
    }
}
