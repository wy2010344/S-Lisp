#pragma once
namespace s{
    namespace list{
        Node * reverse(Node * node)
        {
            Node* r=NULL;
            for(Node *tmp=node;tmp!=NULL;tmp=tmp->Rest())
            {
                r=new Node(tmp->First(),r);
            }
            return r;
        }
        Node* reverseAndDelete(Node* node){
            Node* r=reverse(node);
            delete node;
            return r;
        }
        Node * restAndDelete(Node * b)
        {
            Node *old=b;
            b=b->Rest();
            old->retain();
            b->retain();
            old->release();
            b->eval_release();
            return b;
        }
    };
};