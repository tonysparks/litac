target datalayout = "e-m:w-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-windows-msvc19.15.26726"
attributes #0 = {
    noinline nounwind optnone uwtable "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="false" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" 
}
source_filename = "test.lita"

@D = internal global double 10.000034E3
@YY = global i32 55

%.struct.test = type {
   float, %.struct.anon.0
}


%.struct.anon.0 = type {
   float*
}


define i32 @main(i32 %len,i8** %args) #0 {
   %1 = alloca i32 
   %2 = alloca i8** 
   %3 = icmp sgt i32 %len, 1
   br i1 %3, label %then0, label %else1 
   then0: 
   %4 = alloca i32 
   store i32 1, i32* %4
   %len = load i32, i32* %4
   
   br label %end2 
   else1: 
   %5 = alloca i32 
   store i32 2, i32* %5
   %len = load i32, i32* %5
   
   br label %end2 
   end2: 
   %6 = add i32 1, %len
   ret i32 %6
   
}
