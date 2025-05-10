import { get, ref, push, set, remove } from "firebase/database";
import { db } from "./firebase"; 

export const addGroceryItem = async (userId: string, item: string) => {
    const groceriesRef = ref(db, `groceries/${userId}`);
    const newItemRef = push(groceriesRef);
    await set(newItemRef, item);
  };
  
  export const getGroceryItems = async (userId: string): Promise<string[]> => {
    const groceriesRef = ref(db, `groceries/${userId}`);
    const snapshot = await get(groceriesRef);
    if (!snapshot.exists()) return [];
    return Object.values(snapshot.val());
  };
  
  export const clearGroceryItems = async (userId: string) => {
    const groceriesRef = ref(db, `groceries/${userId}`);
    await remove(groceriesRef);
  };


export const addPantryItem = async (userId: string, item: { name: string; emoji: string }) => {
    const pantryRef = ref(db, `pantry/${userId}`);
    const newItemRef = push(pantryRef);
    await set(newItemRef, item);
};

export const getPantryItems = async (userId: string): Promise<{ name: string; emoji: string }[]> => {
    const pantryRef = ref(db, `pantry/${userId}`);
    const snapshot = await get(pantryRef);
    if (!snapshot.exists()) return [];
    return Object.values(snapshot.val());
};

export const clearPantryItems = async (userId: string) => {
    const pantryRef = ref(db, `pantry/${userId}`);
    await remove(pantryRef);
};

export const removePantryItem = async (userId: string, itemName: string) => {
    const pantryRef = ref(db, `pantry/${userId}`);
    const snapshot = await get(pantryRef);
    if (!snapshot.exists()) return;
  
    const data = snapshot.val();
    const keyToRemove = Object.keys(data).find(key => data[key].name === itemName);
    if (keyToRemove) {
      await remove(ref(db, `pantry/${userId}/${keyToRemove}`));
    }
  };

  export const getMealPlanItems = async (userId: string): Promise<string[]> => {
    const mealPlanRef = ref(db, `mealPlan/${userId}`);
    const snapshot = await get(mealPlanRef);
    if (!snapshot.exists()) return [];
    return Object.values(snapshot.val());
  };
  
  export const addMealPlanItem = async (userId: string, item: string) => {
    const mealPlanRef = ref(db, `mealPlan/${userId}/${item}`);
    await set(mealPlanRef, item);
  };
  
  export const clearMealPlanItems = async (userId: string) => {
    const mealPlanRef = ref(db, `mealPlan/${userId}`);
    await remove(mealPlanRef); 
  };