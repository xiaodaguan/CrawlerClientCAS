 

#include<iostream>
#include<cstring>
#include<vector>
using namespace std;
//str1Ϊ����str2������
const string LCS(const string& str1,const string& str2){
    int xlen=str1.size();       //���򳤶�
    vector<int> tmp(xlen);        //����������һ��
    vector<int> arr(tmp);     //��ǰ��
    int ylen=str2.size();       //���򳤶�
    int maxele=0;               //����Ԫ���е����ֵ
    int pos=0;                  //����Ԫ�����ֵ�����ڵڼ���
    for(int i=0;i<ylen;i++){
        string s=str2.substr(i,1);
        arr.assign(xlen,0);     //������0
        for(int j=0;j<xlen;j++){
            if(str1.compare(j,1,s)==0){
                if(j==0)
                    arr[j]=1;
                else
                    arr[j]=tmp[j-1]+1;
                if(arr[j]>maxele){
                    maxele=arr[j];
                    pos=j;
                }
            }       
        }
//      {
//          vector<int>::iterator iter=arr.begin();
//          while(iter!=arr.end())
//              cout<<*iter++;
//          cout<<endl;
//      }
        tmp.assign(arr.begin(),arr.end());
    }
    string res=str1.substr(pos-maxele+1,maxele);
    return res;
}
int main(){
    string str1("21232523311324");
    string str2("312123223445");
    string lcs=LCS(str1,str2);

    cout << lcs.c_str() << endl;
    return 0;
}