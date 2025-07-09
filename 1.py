n=0
print('大于一百的对偶数：')
for a in range(100,1000):
    if a//100==a%10:
        print(a,end=' ')
        n+=1
    if n==10:
        break
