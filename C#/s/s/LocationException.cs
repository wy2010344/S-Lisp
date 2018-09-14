using System;
using System.Collections.Generic;
using System.Text;

namespace s
{
    public class LocationException:Exception
    {
        private Location loc;
        public LocationException(Location loc, String msg):base(msg)
        {
            this.loc = loc;
        }
        public Location Loc() {
            return loc;
        }

        public override string ToString()
        {
            return base.ToString() + loc.ToString();
        }
    }
}
