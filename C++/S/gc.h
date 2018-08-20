#pragma once

namespace s{
	class Base;
	namespace gc{
	    class LNode{
	    public:
	        Base *value;
	        LNode *next;
	    };
	};
};