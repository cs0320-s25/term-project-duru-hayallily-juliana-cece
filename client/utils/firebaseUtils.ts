import { get, ref, push, set, remove } from "firebase/database";
import { db } from "./firebase";

// Grocery list functions
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

// Pantry functions
export const addPantryItem = async (
  userId: string,
  item: { name: string; emoji: string }
) => {
  const pantryRef = ref(db, `pantry/${userId}`);
  const newItemRef = push(pantryRef);
  await set(newItemRef, item);
};

export const getPantryItems = async (
  userId: string
): Promise<{ name: string; emoji: string }[]> => {
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
  const keyToRemove = Object.keys(data).find(
    (key) => data[key].name === itemName
  );
  if (keyToRemove) {
    await remove(ref(db, `pantry/${userId}/${keyToRemove}`));
  }
};

// Meal plan functions - updated to support recipe objects
export const getMealPlanItems = async (userId: string): Promise<any[]> => {
  const mealPlanRef = ref(db, `mealPlan/${userId}`);
  const snapshot = await get(mealPlanRef);
  if (!snapshot.exists()) return [];
  const values = Object.values(snapshot.val());
  // Handle both old string format and new object format
  return values.map((value) => {
    if (typeof value === "string") {
      try {
        return JSON.parse(value);
      } catch {
        // If it fails to parse, treat as old format
        return { recipeName: value, recipeId: null };
      }
    }
    return value;
  });
};

export const addMealPlanItem = async (
  userId: string,
  item: string | object
) => {
  const mealPlanRef = ref(db, `mealPlan/${userId}`);
  const newItemRef = push(mealPlanRef);
  await set(newItemRef, item);
};

export const clearMealPlanItems = async (userId: string) => {
  const mealPlanRef = ref(db, `mealPlan/${userId}`);
  await remove(mealPlanRef);
};
